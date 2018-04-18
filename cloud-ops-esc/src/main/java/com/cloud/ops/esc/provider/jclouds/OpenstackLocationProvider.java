package com.cloud.ops.esc.provider.jclouds;

import com.cloud.ops.esc.LocationProvider;
import com.cloud.ops.esc.provider.JcloudsLocation;
import com.cloud.ops.esc.wf.WorkFlowExecutor;
import com.cloud.ops.esc.wf.WorkFlowService;
import com.cloud.ops.esc.wf.model.WorkFlowEntity;
import com.cloud.ops.esc.wf.model.WorkFlowStatus;
import com.cloud.ops.tosca.model.Topology;
import com.cloud.ops.tosca.model.definition.ListPropertyValue;
import com.cloud.ops.tosca.model.definition.ScalarPropertyValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static com.cloud.ops.tosca.model.normative.NormativeNodeConstants.COMPUTE_TYPE_OPENSTACK;
import static com.cloud.ops.tosca.model.normative.NormativeNodeConstants.DEPLOY_TYPE;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/23
 */
@Service
public class OpenstackLocationProvider extends JcloudsLocation implements LocationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(JcloudsLocation.class);
    private NovaApi novaApi;
    private NeutronApi neutronApi;
    private String region;
    @Autowired
    private WorkFlowExecutor workFlowExecutor;
    @Autowired
    private WorkFlowService service;

    @Override
    public Topology install(Topology topology, WorkFlowEntity entity, Map<String, Object> inputs) {
        initApi();
        service.save(entity);
        ServerApi serverApi = novaApi.getServerApi(region);
        ImageApi imageApi = novaApi.getImageApi(region);
        FlavorApi flavorApi = novaApi.getFlavorApi(region);
        FloatingIPApi floatingIPApi = novaApi.getFloatingIPApi(region).get();
        NetworkApi networkApi = neutronApi.getNetworkApi(region);

        final Image[] image = new Image[1];
        imageApi.listInDetail().concat().forEach(v -> {
            if (v.getName().equals("service-image-centos7")) {
                image[0] = v;
            }
        });

        final Flavor[] flavor = new Flavor[1];
        flavorApi.listInDetail().concat().forEach(v -> {
            if (v.getName().equals("c1.micro")) {
                flavor[0] = v;
            }
        });

        final Network[] net = new Network[1];
        networkApi.list().concat().forEach(v -> {
            if (!v.getExternal()) {
                net[0] = v;
            }
        });

        String name = "tomcat-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        ServerCreated tomcat = serverApi.create(name, image[0].getId(), flavor[0].getId(),
                CreateServerOptions.Builder
                        .adminPass("123456a?")
                        .networks(net[0].getId())
        );
        String ip = floatingIPApi.list().get(0).getIp();
        System.out.println("server name: " + name + " ip: " + ip);
        boolean isStart = false;
        boolean isBind = false;
        long endWaitTime = System.currentTimeMillis() + 120 * 1000;
        while (System.currentTimeMillis() < endWaitTime && (!isStart || !isBind)) {
            for (Server v : serverApi.listInDetail().concat()) {
                if (v.getName().equals(name) && v.getStatus().equals(Server.Status.ACTIVE)) {
                    if (!isStart) {
                        System.out.println("server has created");
                        floatingIPApi.addToServer(ip, tomcat.getId());
                        System.out.println("ip started bind");
                        isStart = true;
                    }
                    if (v.getAddresses().get(net[0].getName()).stream().anyMatch(s -> s.getAddr().equals(ip))) {
                        isBind = serverListening(ip, 22);
                        if (isBind) {
                            System.out.println("ip can ping");
                        }
                    }
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                LOGGER.error("thread that wait for server start is interrupted", e);
            }
        }

        topology.getNodeTemplatesOfType(COMPUTE_TYPE_OPENSTACK).forEach(node -> {
            node.getAttributes().put("hosts", new ListPropertyValue(ImmutableList.of(ip)));
            node.getAttributes().put("user", new ScalarPropertyValue("root"));
            node.getAttributes().put("password", new ScalarPropertyValue("123456a?"));
        });
        entity.setEndAt(new Date());
        entity.setStatus(WorkFlowStatus.SUCCESS);
        service.save(entity);
        topology.getNodeTemplatesOfType(DEPLOY_TYPE).forEach(v -> {
            WorkFlowEntity wf = new WorkFlowEntity();
            wf.setStartAt(new Date());
            wf.setName("create");
            wf.setObjectId(entity.getObjectId());
            wf.setNodeName(v.getName());
            System.out.println("wf create " + v.getName());
            workFlowExecutor.executeWorkFlow(topology, wf, inputs);
        });
        return topology;
    }

    @Override
    public void executeWorkFlow(Topology topology, WorkFlowEntity entity, Map<String, Object> inputs) {
        workFlowExecutor.executeWorkFlow(topology, entity, inputs);
    }

    private void initApi() {
        Iterable<Module> modules = ImmutableSet.of(new SLF4JLoggingModule());
        novaApi = ContextBuilder.newBuilder("openstack-nova")
                .endpoint(getEndpoint())
                .credentials(getIdentity(), getCredential())
                .modules(modules)
                .buildApi(NovaApi.class);
        neutronApi = ContextBuilder.newBuilder("openstack-neutron")
                .endpoint(getEndpoint())
                .credentials(getIdentity(), getCredential())
                .modules(modules)
                .buildApi(NeutronApi.class);
        region = (String) novaApi.getConfiguredRegions().toArray()[0];
    }

    public static boolean serverListening(String host, int port) {
        Socket s = null;
        try {
            s = new Socket(host, port);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (s != null)
                try {
                    s.close();
                } catch (Exception e) {
                }
        }
    }
}
