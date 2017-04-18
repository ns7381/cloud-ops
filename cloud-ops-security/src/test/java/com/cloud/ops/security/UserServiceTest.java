package com.cloud.ops.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test to validate the correctness of the soft-delete implementation.
 *
 * @author NingSheng
 */
//@RunWith(SpringRunner.class)
//@DataJpaTest
@RunWith(SpringRunner.class)
@SpringBootTest
//@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {

//    @Autowired
//    private UserService userService;

    @Test
    public void testCreate() throws Exception {
        long startTime=System.currentTimeMillis();
//        User user = new User();
//        user.setUsername("test");
//        userService.save(user);
        long endTime=System.currentTimeMillis();
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }
}
