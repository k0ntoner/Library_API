package org.example.controllers;

import org.example.entities.User;
import org.example.services.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public boolean isPhoneNumberExists(String phoneNumber) throws IOException, SQLException {
        return userService.isPhoneNumberExists(phoneNumber);
    }

    public User getUser(String id) throws IOException, SQLException {
        return userService.findById(id);
    }

    public Collection<User> getUsers() throws IOException, SQLException {
        return userService.findAll();
    }

}
