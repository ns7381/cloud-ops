package com.cloud.ops.esc.openstack;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.io.Payloads;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.ssh.SshClient;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/23
 */
public class OpenstackTest {
    private NovaApi novaApi;
    private NeutronApi neutronApi;
    private String region;
    private ComputeServiceContext context;

    @Before
    public void before() {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        String provider = "openstack-nova";
        String identity = "admin:admin"; // tenantName:userName
        String credential = "123456a?";

        novaApi = ContextBuilder.newBuilder(provider)
                .endpoint("http://10.110.17.19:5000/v2.0/")
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(NovaApi.class);

        neutronApi = ContextBuilder.newBuilder("openstack-neutron")
                .endpoint("http://10.110.17.19:5000/v2.0/")
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(NeutronApi.class);

        context = ContextBuilder.newBuilder(provider)
                .endpoint("http://10.110.17.19:5000/v2.0/")
                .credentials(identity, credential)
                .modules(modules)
                .buildView(ComputeServiceContext.class);
        region = (String) novaApi.getConfiguredRegions().toArray()[0];
    }

    @Test
    public void testCreateWithApi() {
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
        System.out.println("-----------------------------------------------------------------------");
        System.out.println(image[0]);

        final Flavor[] flavor = new Flavor[1];
        flavorApi.listInDetail().concat().forEach(v -> {
            if (v.getName().equals("c1.micro")) {
                flavor[0] = v;
            }
        });
        System.out.println("-----------------------------------------------------------------------");
        System.out.println(flavor[0]);

        final Network[] net = new Network[1];
        networkApi.list().concat().forEach(v -> {
            if (v.getName().equals("int-net")) {
                net[0] = v;
            }
        });
        System.out.println("-----------------------------------------------------------------------");
        System.out.println(net[0]);
        ServerCreated tomcat = serverApi.create("tomcat", image[0].getId(), flavor[0].getId(),
                CreateServerOptions.Builder
                        .adminPass("123456a?")
                        .networks(net[0].getId())
        );
        String ip = floatingIPApi.list().get(0).getIp();
        System.out.println("-----------------------------------------------------------------------");
        System.out.println(ip);
        floatingIPApi.addToServer(ip, tomcat.getId());
    }

    @Test
    public void testBindFloatingIp() throws RunNodesException {
        FloatingIPApi floatingIPApi = novaApi.getFloatingIPApi(region).get();
        String ip = floatingIPApi.list().get(0).getIp();
        System.out.println("-----------------------------------------------------------------------");
        System.out.println(ip);
        floatingIPApi.addToServer(ip, "7b567a7a-427c-4850-92c3-3c59be2d9814");
    }

    @Test
    public void testListNetwork() throws RunNodesException {
        NetworkApi networkApi = neutronApi.getNetworkApi(region);
        networkApi.list().concat().forEach(System.out::println);
    }

    @Test
    public void testListServer() throws RunNodesException {
        ServerApi serverApi = novaApi.getServerApi(region);
        serverApi.listInDetail().concat().forEach(System.out::println);
    }

    @Test
    public void testCreateWithComputeService() throws RunNodesException {
        ComputeService client = context.getComputeService();
        client.listImages().forEach(System.out::println);
        Template template = client.templateBuilder()
                .imageNameMatches("service-image-centos7")
                .options(TemplateOptions.Builder.networks("ccf130b0-f16c-42cb-b356-2514deb4b7cd"))
                .minCores(1)
                .minRam(1000)
                .build();

        Set<? extends NodeMetadata> tomcat = client.createNodesInGroup("tomcat", 1, template);
        System.out.println(tomcat);
    }

    @Test
    public void testSSH() throws RunNodesException {
        ComputeService client = context.getComputeService();
        final NodeMetadata[] node = new NodeMetadata[1];
        client.listNodes().forEach(v -> {
            if (v.getName().equals("tomcat")) {
                node[0] = (NodeMetadata) v;
            }
        });
        SshClient sshClient = context.utils().sshForNode().apply(
                NodeMetadataBuilder.fromNodeMetadata(node[0]).credentials(
                        LoginCredentials.builder().user("root").password("123456a?").build()).build());
        sshClient.put("/path/to/file", Payloads.newFilePayload(new File("F:\\temp\\cloud-ops.log")));
    }

    public void close() throws IOException {
        Closeables.close(novaApi, true);
    }
}
