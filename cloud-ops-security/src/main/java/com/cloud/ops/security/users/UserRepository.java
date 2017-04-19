package com.cloud.ops.security.users;

import com.cloud.ops.security.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface UserRepository extends JpaRepository<User, Serializable> {
    User findByUsername(String username);
}
