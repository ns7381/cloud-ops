package com.cloud.ops.security.users.rest;

import com.cloud.ops.security.modal.Role;
import com.cloud.ops.security.modal.User;
import com.cloud.ops.security.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {


    @GetMapping
    public UserDto auth() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final UserDto userStatus = new UserDto();
        if (auth == null) {
            userStatus.setIsLogged(false);
        } else {
            userStatus.setIsLogged(auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken));
            userStatus.setUsername(auth.getName());
            for (GrantedAuthority role : auth.getAuthorities()) {
                userStatus.getRoles().add(role.getAuthority());
            }
        }
        return userStatus;
    }
}
