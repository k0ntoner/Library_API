package org.example.controllers;

import org.example.entities.Customer;
import org.example.services.CustomerService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Collection<Customer> getCustomers() throws SQLException, IOException {
        return customerService.findAll();
    }

    public Customer getCustomerById(String id) throws SQLException, IOException {
        return customerService.findById(id);
    }

    public Customer createCustomer(Customer customer) throws SQLException, IOException {
        return customerService.save(customer);
    }

    public Customer updateCustomer(Customer customer) throws SQLException, IOException {
        return customerService.update(customer);
    }

    public void deleteCustomerById(String id) throws SQLException, IOException {
        customerService.deleteById(id);
    }
}
