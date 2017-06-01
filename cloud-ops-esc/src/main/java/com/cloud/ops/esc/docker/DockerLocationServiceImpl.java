package com.cloud.ops.esc.docker;


import com.cloud.ops.esc.Location;
import com.cloud.ops.esc.LocationConstants;
import com.cloud.ops.esc.LocationService;
import com.cloud.ops.toscamodel.impl.TopologyContext;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Template;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;

@Service
public class DockerLocationServiceImpl implements LocationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerLocationServiceImpl.class);

    // get a context with docker that offers the portable ComputeService api
    public ComputeServiceContext getComputeServiceContext(Location location) {
        Map<String, Object> metaProperties = location.getMetaProperties();
        // get a context with docker that offers the portable ComputeService api "https://10.110.19.226:2376"
        ContextBuilder contextBuilder = ContextBuilder.newBuilder("docker").endpoint((String) metaProperties.get("endpoint"))
                .credentials((String)metaProperties.get("cert.pem"), (String)metaProperties.get("key.pem"))
                .modules(ImmutableSet.<Module>of(new Log4JLoggingModule(),
                        new SshjSshClientModule()));

        Properties props = new Properties();

        props.setProperty("docker.cacert.path", (String)metaProperties.get("ca.pem"));
        contextBuilder.overrides(props);

        ComputeServiceContext context = contextBuilder.buildView(ComputeServiceContext.class);
        return context;
//        ComputeService client = context.getComputeService();
//        System.out.println(client.listImages());
//        this can be obtained using `docker images --no-trunc` command
//        String sshableImageId = "48b5124b2768d2b917edcb640435044a97967015485e812545546cbed5cf0233";
//        Template template = client.templateBuilder().imageId(sshableImageId).build();

//        run a couple nodes accessible via group container
//        Set<? extends NodeMetadata> nodes = client.createNodesInGroup("container", 2, template);

//        release resources
//        context.close();

    }

    @Override
    public TopologyContext install(TopologyContext topologyContext, Location location) {
        ComputeServiceContext context = this.getComputeServiceContext(location);
        ComputeService computeService = context.getComputeService();
        topologyContext.getNodeTemplateMap().forEach((name, node) -> {
            if (LocationConstants.LOCATION_TYPE_DOCKER.equals(node.getType())) {
                Template image = computeService.templateBuilder().imageId((String) node.getAttributes().get("image")).build();
                try {
                    computeService.createNodesInGroup("container", 1, image);
                } catch (RunNodesException e) {
                    LOGGER.error(e.getMessage(), e);
                    new RuntimeException("create container error");
                }
            }
        });
        context.close();
        return topologyContext;
    }

    @Override
    public TopologyContext executeWorkFlow(TopologyContext topologyContext, Location location) {
        return topologyContext;
    }
}
