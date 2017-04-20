package com.cloud.ops.esc.docker;


import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

import java.util.Properties;
import java.util.Set;

/**
 * Created by shelan on 1/25/16.
 */
public class DockerLauncher {
    // get a context with docker that offers the portable ComputeService api
    public static void main(String[] args) throws RunNodesException {

        String email = "E:\\learn\\docker\\DockerJcloudsTest\\src\\main\\java\\kth\\se\\cert.pem";
        String password = "E:\\learn\\docker\\DockerJcloudsTest\\src\\main\\java\\kth\\se\\key.pem";

        // get a context with docker that offers the portable ComputeService api
        ContextBuilder contextBuilder = ContextBuilder.newBuilder("docker").endpoint("https://10.110.19.226:2376")
                .credentials(email, password)
                .modules(ImmutableSet.<Module>of(new Log4JLoggingModule(),
                        new SshjSshClientModule()));

        Properties props = new Properties();

        props.setProperty("docker.cacert.path","E:\\learn\\docker\\DockerJcloudsTest\\src\\main\\java\\kth\\se\\ca.pem");
        contextBuilder.overrides(props);

        ComputeServiceContext context = contextBuilder.buildView(ComputeServiceContext.class);
        ComputeService client = context.getComputeService();

        String sshableImageId = "48b5124b2768d2b917edcb640435044a97967015485e812545546cbed5cf0233"; // this can be obtained using `docker images --no-trunc` command
        Template template = client.templateBuilder().imageId(sshableImageId).build();

// run a couple nodes accessible via group container
        Set<? extends NodeMetadata> nodes = client.createNodesInGroup("container", 2, template);

// release resources
        context.close();

    }
}
