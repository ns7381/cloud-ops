package com.cloud.ops.core;

import com.cloud.ops.core.application.ApplicationService;
import com.cloud.ops.core.model.application.Application;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * Test to validate the correctness of the soft-delete implementation.
 *
 * @author NingSheng
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationServiceTest {

    @Autowired
    private ApplicationService applicationService;

    @Test
    public void testCreate() throws Exception {
        Application application = new Application();
        application.setName("test");
        application.setTopologyId("9e35f0ca450e42a3904e5d24a4e38833");
        List<String> hosts = Lists.newArrayList();
        hosts.add("10.110.20.147");
        /*LocalLocation localLocation = LocalLocation.builder()
                .hosts(hosts)
                .user("root")
                .password("123456a?").build();
        HashMap<String, LocalLocation> map = new HashMap<>();
        map.put("mysql_host", localLocation);
        map.put("java_host", localLocation);
        application.setLocations(map);*/
        application.setEnvironmentId("123");
        applicationService.create(application);
    }

    @Test
    public void testUpdate() throws Exception {
        long startTime = System.currentTimeMillis();
        Application application = new Application();
        application.setId("8a48aca65b517c12015b517c29ed0000");
        application.setName("test");
        application.setTopologyId("9e35f0ca450e42a3904e5d24a4e38833");
        List<String> hosts = Lists.newArrayList();
        hosts.add("10.110.20.147");
        /*LocalLocation localLocation = LocalLocation.builder()
                .host(hosts)
                .user("root")
                .password("123456a?").build();
        HashMap<String, LocalLocation> map = new HashMap<>();
        map.put("mysql_host", localLocation);
        map.put("java_host", localLocation);
        application.setEnvironmentId("8a48aca65b479025015b4b255da30000");
        applicationService.create(application);
        long endTime=System.currentTimeMillis();
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");*/
    }

    @Test
    public void testGet() {
//        Application application = applicationService.get("8a48aca65b56ac30015b56b17c8f0000");
//        System.out.println(application);
//        applicationService.findByEnvironmentId("8a48aca65b479025015b4b255da30000");
        Application application = applicationService.get("8a48aca65b66a399015b66cf2b740000");
        System.out.println(application);
        Map<String, Object> map = Maps.newHashMap();
        map.put("tomcat_home", "/opt/tomcat123456");
        applicationService.changeApplicationAttributes("8a48aca65b66a399015b66cf2b740000", "tomcat", map);
        Application application1 = applicationService.get("8a48aca65b66a399015b66cf2b740000");
        System.out.println(application1);
    }

    @Test
    public void testDeploy() {
//        applicationService.deploy("8a48aca65b56ac30015b56b17c8f0000", "tomcat", "8a48aca65b5bc49e015b5c3870010003");
//        applicationService.deploy("402882e55b57fc3a015b57ffd6920000", "tomcat", "8a48aca65b5bc49e015b5c3870010003");
    }
}
