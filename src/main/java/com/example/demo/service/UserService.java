package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Role;
import com.example.demo.model.User;

public interface UserService {
    public User saveUser (User user);
    public Role saveRole(Role role);
    public void addRoleToUser(String username, String roleName);
    public User getUser(String username);
    public List<User> getUsers();
}
