package com.cloud.ops;

import com.cloud.ops.core.application.repository.ApplicationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ApplicationRepository applicationRepository;

    @Test
    public void testDeletion() throws Exception {
        // Validates if there are no users in the repository.
        assertEquals(0, applicationRepository.count());
//        assertEquals(0, applicationRepository.countDeletedEntries());

        // Creates a new application and saves it.
        com.cloud.ops.core.model.application.Application application = new com.cloud.ops.core.model.application.Application();
        application.setName("foo");
        application.setDescription("bar");
        com.cloud.ops.core.model.application.Application db = applicationRepository.saveAndFlush(application);

        // Now there is one application in the Database.
        assertEquals(1, applicationRepository.count());
//        assertEquals(0, applicationRepository.countDeletedEntries());

        db = applicationRepository.findOne(db.getId());
        // Deletes the application.
        applicationRepository.delete(db.getId());

        // Ensures that the repository-methods doesn't return the deleted application.
        assertEquals(0, applicationRepository.count());
        // But there should be one application which is still reachable by a native sql-query.
//        assertEquals(1, applicationRepository.countDeletedEntries());
    }
}
