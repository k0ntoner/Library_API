package org.example.controllers;

import org.example.entities.Employee;
import org.example.services.EmployeeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public Collection<Employee> getEmployees() throws SQLException, IOException {
        return employeeService.findAll();
    }

    public Employee getEmployeeById(String id) throws SQLException, IOException {
        return employeeService.findById(id);
    }

    public Employee createEmployee(Employee employee) throws SQLException, IOException {
        return employeeService.save(employee);
    }

    public Employee updateEmployee(Employee employee) throws SQLException, IOException {
        return employeeService.update(employee);
    }

    public void deleteEmployeeById(String id) throws SQLException, IOException {
        employeeService.deleteById(id);
    }
}
