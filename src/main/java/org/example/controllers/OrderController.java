package org.example.controllers;

import org.example.entities.BookCopy;
import org.example.entities.Order;
import org.example.services.BookCopyService;
import org.example.services.OrderService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public Collection<Order> getOrders() throws SQLException, IOException {
        return orderService.findAll();
    }

    public Order getOrderById(Long id) throws SQLException, IOException {
        return orderService.findById(id);
    }

    public Order createOrder(Order order) throws SQLException, IOException {
        return orderService.save(order);
    }

    public Order updateOrder(Order order) throws SQLException, IOException {
        return orderService.update(order);
    }

    public void deleteOrderById(Long id) throws SQLException, IOException {
        orderService.deleteById(id);
    }
}
