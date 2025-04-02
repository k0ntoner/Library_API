package org.example.services.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.Employee;
import org.example.repositories.EmployeeRepository;
import org.example.repositories.UserRepository;
import org.example.services.EmployeeService;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private UserRepository userRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Employee save(Employee entity) throws SQLException {
        log.info("Request to save Employee : {}", entity);
        if (userRepository.findByPhoneNumber(entity.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("User with such phone number: {" + entity.getPhoneNumber() + "}, already exists");
        }
        return employeeRepository.save(entity);
    }

    @Override
    public Employee findById(String id) throws SQLException {
        log.info("Request to find Employee by id : {}", id);
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return employee.get();
        }
        throw new SQLException("Employee id: {" + id + "} not found");
    }

    @Override
    public Employee update(Employee entity) throws SQLException {
        log.info("Request to update Employee : {}", entity);
        Employee foundEmployee = findById(entity.getId());
        if (foundEmployee.getEmail().equals(entity.getEmail())) {
            return employeeRepository.update(entity);
        }
        throw new IllegalArgumentException("While updating employee is promised to change email");
    }

    @Override
    public void delete(Employee entity) throws SQLException {
        log.info("Request to delete Employee : {}", entity);
        employeeRepository.delete(entity);
    }

    @Override
    public void deleteById(String id) throws SQLException {
        employeeRepository.delete(findById(id));
    }

    @Override
    public Collection<Employee> findAll() throws SQLException {
        log.info("Request to find all Employees");
        return employeeRepository.findAll();
    }
}
