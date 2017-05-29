package com.cloud.ops;

import com.cloud.ops.core.model.Resource.ResourcePackageConfig;
import com.cloud.ops.core.resource.ResourcePackageConfigService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test to validate the correctness of the soft-delete implementation.
 *
 * @author NingSheng
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class ResourcePackageConfigServiceTest {

    @Autowired
    private ResourcePackageConfigService service;

    @Test
    public void testFindByApplicationId() throws Exception {
        long startTime=System.currentTimeMillis();
        ResourcePackageConfig resourcePackageConfig = new ResourcePackageConfig();
        resourcePackageConfig.setId("123");
        resourcePackageConfig.setApplicationId("234");
        service.create(resourcePackageConfig);
        service.create(resourcePackageConfig);
        ResourcePackageConfig packageConfig = service.findByApplicationId("234");
        long endTime=System.currentTimeMillis();
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }
}
