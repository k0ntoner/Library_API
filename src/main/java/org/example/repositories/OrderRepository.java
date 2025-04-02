package org.example.repositories;

import org.example.entities.Order;

import java.sql.SQLException;
import java.util.Collection;

public interface OrderRepository extends BasicRepository<Order> {
    Collection<Order> findByUserId(String user_id) throws SQLException;

}
