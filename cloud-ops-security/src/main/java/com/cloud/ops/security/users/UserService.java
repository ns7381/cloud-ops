package com.cloud.ops.security.users;

import com.cloud.ops.security.modal.User;
import com.cloud.ops.security.modal.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        List<UserRole> roles = user.getRoles();
        for (UserRole role : roles) {
            role.setUserId(user.getId());
            userRoleRepository.save(role);
        }
        return user;
    }

    public void delete(String id) {
        userRepository.delete(id);
        userRoleRepository.delete(userRoleRepository.findByUserId(id));
    }

    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            user.setRoles(userRoleRepository.findByUserId(user.getId()));
        });
        return users;
    }

    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        user.setRoles(userRoleRepository.findByUserId(user.getId()));
        return user;
    }
}
