package com.cloud.ops;

import com.cloud.ops.entity.application.Application;
import com.cloud.ops.entity.application.Host;
import com.cloud.ops.service.ApplicationService;
import com.cloud.ops.service.TopologyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * Test to validate the correctness of the soft-delete implementation.
 *
 * @author NingSheng
 */
//@RunWith(SpringRunner.class)
//@DataJpaTest
@RunWith(SpringRunner.class)
@SpringBootTest
public class TopologyServiceTest {

    @Autowired
    private TopologyService topologyService;

    @Test
    public void testGet() throws Exception {
        long startTime=System.currentTimeMillis();
        topologyService.get("9e35f0ca450e42a3904e5d24a4e38833");
        long endTime=System.currentTimeMillis();
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }
}
