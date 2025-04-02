package org.example.services;

import org.example.entities.User;

import java.sql.SQLException;
import java.util.Collection;

public interface UserService {
    User findByEmail(String username) throws SQLException;

    boolean isPhoneNumberExists(String phoneNumber) throws SQLException;

    User findById(String id) throws SQLException;

    Collection<User> findAll() throws SQLException;

}
