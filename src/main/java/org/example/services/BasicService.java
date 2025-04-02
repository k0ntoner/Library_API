package org.example.services;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface BasicService<T> {
    T save(T entity) throws SQLException;

    T findById(Long id) throws SQLException;

    T update(T entity) throws SQLException;

    void delete(T entity) throws SQLException;

    void deleteById(Long id) throws SQLException;

    Collection<T> findAll() throws SQLException;

}
