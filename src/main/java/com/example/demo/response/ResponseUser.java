package com.example.demo.response;

import java.util.Collection;

import com.example.demo.model.Role;

import lombok.Data;

@Data
public class ResponseUser {
    private String name;
    private String username;
    private Collection<Role> roles;
}
