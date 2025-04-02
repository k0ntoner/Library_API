package org.example.repositories;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface BasicRepository<T> {
    T save(T entity) throws SQLException;

    Optional<T> findById(Long id) throws SQLException;

    T update(T entity) throws SQLException;

    void delete(T entity) throws SQLException;

    Collection<T> findAll() throws SQLException;

}
