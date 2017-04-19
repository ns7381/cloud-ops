package com.cloud.ops.security;

import com.cloud.ops.security.modal.Role;
import com.cloud.ops.security.modal.User;
import com.cloud.ops.security.modal.UserRole;
import com.cloud.ops.security.users.UserService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private UserService userService;

    @Test
    public void testCreate() throws Exception {
        long startTime=System.currentTimeMillis();
        User user = new User();
        user.setUsername("admin");
        user.setPassword("123456a?");
        UserRole userRole = new UserRole();
        userRole.setRole(Role.ADMIN);
        List<UserRole> roles = Lists.newArrayList();
        roles.add(userRole);
        user.setRoles(roles);
        userService.save(user);
        long endTime=System.currentTimeMillis();
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }


    @Test
    public void findByUsername() {
        User user = userService.findByUsername("admin");
        System.out.println(user);
    }
}
