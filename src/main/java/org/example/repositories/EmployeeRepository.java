package org.example.repositories;

import org.example.entities.Customer;
import org.example.entities.Employee;
import org.example.entities.Order;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface EmployeeRepository {
    Employee save(Employee entity) throws SQLException;

    Optional<Employee> findById(String id) throws SQLException;

    Employee update(Employee entity) throws SQLException;

    void delete(Employee entity) throws SQLException;

    Collection<Employee> findAll() throws SQLException;
}
