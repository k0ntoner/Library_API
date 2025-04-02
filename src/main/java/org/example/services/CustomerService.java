package org.example.services;

import org.example.entities.Customer;
import org.example.entities.Order;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface CustomerService {
    Customer save(Customer entity) throws SQLException;

    Customer findById(String id) throws SQLException;

    Customer update(Customer entity) throws SQLException;

    void delete(Customer entity) throws SQLException;

    void deleteById(String id) throws SQLException;

    Collection<Customer> findAll() throws SQLException;

    Collection<Order> findAllOrdersByEmail(String email) throws SQLException;
}
