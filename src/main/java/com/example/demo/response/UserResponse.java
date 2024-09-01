package com.example.demo.response;

import java.util.Collection;

import org.springframework.hateoas.server.core.Relation;

import com.example.demo.model.Role;

import lombok.Data;

@Data
@Relation(collectionRelation = "users")
public class UserResponse {
    private String name;
    private String username;
    private Collection<Role> roles;
}
