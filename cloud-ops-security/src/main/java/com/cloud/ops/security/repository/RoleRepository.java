package com.cloud.ops.security.repository;

import com.cloud.ops.security.modal.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
