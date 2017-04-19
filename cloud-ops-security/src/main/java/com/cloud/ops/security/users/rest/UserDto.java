package com.cloud.ops.security.users.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
public class UserDto {
    private Boolean isLogged;
    private String username;
    private Collection<String> roles = new ArrayList<String>();
}