package com.cloud.ops;

import com.cloud.ops.entity.application.Application;
import com.cloud.ops.entity.application.Host;
import com.cloud.ops.repository.ApplicationRepository;
import com.cloud.ops.service.ApplicationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

/**
 * Test to validate the correctness of the soft-delete implementation.
 *
 * @author NingSheng
 */
//@RunWith(SpringRunner.class)
//@DataJpaTest
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
        Host host = new Host();
        host.setIp("10.110.20.155");
        host.setUsername("root");
        host.setPassword("123456a?");
        Host host2 = new Host();
        host2.setIp("10.110.20.147");
        host2.setUsername("root");
        host2.setPassword("123456a?");
        HashMap<String, Host> map = new HashMap<>();
        map.put("mysql_host", host);
        map.put("java_host", host2);
        application.setHosts(map);
        application.setEnvironmentId("123");
        applicationService.create(application);
    }

    @Test
    public void testUpdate() throws Exception {
        long startTime=System.currentTimeMillis();
        Application application = new Application();
        application.setId("8a48aca65b517c12015b517c29ed0000");
        application.setName("test");
        application.setTopologyId("9e35f0ca450e42a3904e5d24a4e38833");
        Host host = new Host();
        host.setIp("10.110.20.155");
        host.setUsername("root");
        host.setPassword("123456a?");
        Host host2 = new Host();
        host2.setIp("10.110.20.147");
        host2.setUsername("root");
        host2.setPassword("123456a?");
        HashMap<String, Host> map = new HashMap<>();
        map.put("mysql_host", host);
        map.put("java_host", host2);
        application.setHosts(map);
        application.setEnvironmentId("8a48aca65b479025015b4b255da30000");
        applicationService.create(application);
        long endTime=System.currentTimeMillis();
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }
}
