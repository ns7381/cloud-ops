package com.cloud.ops;

import com.cloud.ops.entity.Resource.ResourcePackage;
import com.cloud.ops.entity.Resource.ResourcePackageConfig;
import com.cloud.ops.service.ResourcePackageService;
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
public class ResourcePackageServiceTest {

    @Autowired
    private ResourcePackageService service;

    @Test
    public void testPackageWar() throws Exception {
        ResourcePackage resourcePackage = new ResourcePackage();
        resourcePackage.setApplicationId("8a48aca65b56ac30015b56b17c8f0000");
        service.packageWar(resourcePackage);
    }
}
