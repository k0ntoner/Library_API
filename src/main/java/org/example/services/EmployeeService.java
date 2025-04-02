package org.example.services;

import org.example.entities.Employee;
import org.example.repositories.EmployeeRepository;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface EmployeeService {
    Employee save(Employee entity) throws SQLException;

    Employee findById(String id) throws SQLException;

    Employee update(Employee entity) throws SQLException;

    void delete(Employee entity) throws SQLException;

    void deleteById(String id) throws SQLException;

    Collection<Employee> findAll() throws SQLException;

}
