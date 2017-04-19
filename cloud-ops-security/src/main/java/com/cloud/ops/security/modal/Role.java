package com.cloud.ops.security.modal;

import com.cloud.ops.common.exception.NotFoundException;

public enum Role {
    ADMIN,
    APPLICATIONS_MANAGER;

    public static String getStringFormatedRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new NotFoundException("Role [" + role + "] cannot be found");
        }
        String goodRoleToAdd = role.toUpperCase();
        try {
            return Role.valueOf(goodRoleToAdd).toString();
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Role [" + role + "] cannot be found");
        }
    }
}
