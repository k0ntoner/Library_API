package org.example.services.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.Customer;
import org.example.entities.Employee;
import org.example.entities.Order;
import org.example.repositories.CustomerRepository;
import org.example.repositories.UserRepository;
import org.example.services.CustomerService;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;

    private UserRepository userRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Collection<Order> findAllOrdersByEmail(String email) throws SQLException {
        log.info("Request to find all Orders by email {}", email);
        return customerRepository.findAllOrdersByEmail(email);
    }

    @Override
    public Customer save(Customer entity) throws SQLException {
        log.info("Request to save Customer {}", entity);
        if (userRepository.findByPhoneNumber(entity.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("User with such phone number: {" + entity.getPhoneNumber() + "}, already exists");
        }
        return customerRepository.save(entity);
    }

    @Override
    public Customer findById(String id) throws SQLException {
        log.info("Request to find Customer by id {}", id);
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            return customer.get();
        }
        throw new SQLException("Customer with id: {" + id + "}, not found");
    }

    @Override
    public Customer update(Customer entity) throws SQLException {
        log.info("Request to update Customer {}", entity);
        Customer foundCustomer = findById(entity.getId());
        if (foundCustomer.getEmail().equals(entity.getEmail())) {
            if (userRepository.findByPhoneNumber(entity.getPhoneNumber()).isEmpty()) {
                return customerRepository.update(entity);
            }
            throw new IllegalArgumentException("User with such phone number: {" + entity.getPhoneNumber() + "}, already exists");
        }
        throw new IllegalArgumentException("While updating customer is promised to change email");
    }

    @Override
    public void delete(Customer entity) throws SQLException {
        log.info("Request to delete Customer {}", entity);
        customerRepository.delete(entity);
    }

    @Override
    public void deleteById(String id) throws SQLException {
        customerRepository.delete(findById(id));
    }

    @Override
    public Collection<Customer> findAll() throws SQLException {
        log.info("Request to find all Customers");
        return customerRepository.findAll();
    }
}
