package org.example.services;

import org.example.entities.Order;

import java.sql.SQLException;
import java.util.Collection;

public interface OrderService extends BasicService<Order> {
    Collection<Order> findByEmail(String email) throws SQLException;
}
