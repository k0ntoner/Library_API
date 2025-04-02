package org.example.repositories;

import org.example.entities.User;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email) throws SQLException;

    Optional<User> findById(String id) throws SQLException;

    Optional<User> findByPhoneNumber(String phoneNumber) throws SQLException;

    boolean isUserHasOverdue(String id) throws SQLException;

    Collection<User> findAll() throws SQLException;
}
