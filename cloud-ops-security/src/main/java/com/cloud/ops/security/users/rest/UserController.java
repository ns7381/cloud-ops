package com.cloud.ops.security.users.rest;

import com.cloud.ops.security.modal.Role;
import com.cloud.ops.security.modal.User;
import com.cloud.ops.security.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public User create(@RequestBody User user) {
        return userService.save(user);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Boolean delete(@PathVariable String id) {
        userService.delete(id);
        return Boolean.TRUE;
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public User update(@RequestBody User user) {
        Assert.hasLength(user.getId(), "user id is required");
        return userService.save(user);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping(value = "/roles")
    public List<String> getRoles() {
        return Arrays.asList(Role.values()).stream().map(Enum::name).collect(Collectors.toList());
    }
}
