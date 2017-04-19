package com.cloud.ops.security.users;

import com.cloud.ops.security.modal.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Serializable> {
    List<UserRole> findByUserId(String userId);
}
