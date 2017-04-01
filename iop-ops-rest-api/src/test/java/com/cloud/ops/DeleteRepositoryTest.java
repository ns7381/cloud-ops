package com.cloud.ops;

import com.cloud.ops.dao.DeploymentDao;
import com.cloud.ops.entity.deployment.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
public class DeleteRepositoryTest {

    @Autowired
    private DeploymentDao deploymentDao;

    @Test
    public void testDeletion() throws Exception {
        // Validates if there are no users in the repository.
        assertEquals(0, deploymentDao.count());
        assertEquals(0, deploymentDao.countDeletedEntries());

        // Creates a new deployment and saves it.
        Deployment deployment = new Deployment();
        deployment.setName("foo");
        deployment.setDescription("bar");
        Deployment db = deploymentDao.saveAndFlush(deployment);

        // Now there is one deployment in the Database.
        assertEquals(1, deploymentDao.count());
        assertEquals(0, deploymentDao.countDeletedEntries());

        db = deploymentDao.findOne(db.getId());
        // Deletes the deployment.
        deploymentDao.delete(db.getId());

        // Ensures that the repository-methods doesn't return the deleted deployment.
        assertEquals(0, deploymentDao.count());
        // But there should be one deployment which is still reachable by a native sql-query.
        assertEquals(1, deploymentDao.countDeletedEntries());
    }
}
