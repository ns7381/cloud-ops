package com.cloud.ops.esc.docker;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.apache.commons.io.IOUtils;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.docker.DockerApi;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

import java.io.*;
import java.util.Properties;

/**
 * Created by ningsheng on 2017/6/2.
 */
public class DockerTest {
    public static void main(String[] args) throws IOException {
        // get a context with docker that offers the portable ComputeService api "https://10.110.19.226:2376"
        ContextBuilder contextBuilder = ContextBuilder.newBuilder("docker").endpoint("https://10.110.19.226:2376")
                .credentials("E:\\iop-ops\\cloud-ops\\cloud-ops-esc\\src\\main\\resources\\cert.pem",
                        "E:\\iop-ops\\cloud-ops\\cloud-ops-esc\\src\\main\\resources\\key.pem")
                .modules(ImmutableSet.<Module>of(new Log4JLoggingModule(),
                        new SshjSshClientModule()));

        Properties props = new Properties();

        props.setProperty("docker.cacert.path", "E:\\iop-ops\\cloud-ops\\cloud-ops-esc\\src\\main\\resources\\ca.pem");
        contextBuilder.overrides(props);

        ComputeServiceContext context = contextBuilder.buildView(ComputeServiceContext.class);
        ComputeService client = context.getComputeService();
        System.out.println(client.listNodes());
        DockerApi dockerApi = contextBuilder.buildApi(DockerApi.class);
        InputStream ff9bd3c066611 = dockerApi.getContainerApi().attach("ff9bd3c06661");
        IOUtils.copy(ff9bd3c066611, new FileOutputStream("test"));
//        ff9bd3c066611.
//        InputStream ff9bd3c06661 = dockerApi.getContainerApi().copy("ff9bd3c06661", Resource.create("/opt/ca.pem"));
//        client.
//        client.runScriptOnNode("ff9bd3c06661", "echo 123");
        //create
        /*final String[] imageId = new String[]{""};
        client.listImages().forEach(image -> {
            if ("tomcat".equals(image.getName())) {
                imageId[0] = image.getId();
            }
        });
        assert imageId.length > 0;
        Template template = client.templateBuilder().imageId(imageId[0]).build();
        try {
            Set<? extends NodeMetadata> tomcat = client.createNodesInGroup("tomcat", 1, template);
        } catch (RunNodesException e) {
            e.printStackTrace();
        }*/
    }
}
