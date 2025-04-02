package org.example.services.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.dtos.LoginUserDto;
import org.example.entities.User;
import org.example.repositories.OrderRepository;
import org.example.repositories.UserRepository;
import org.example.services.UserService;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public UserServiceImpl(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        log.info("Request to find User by email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setOrders(orderRepository.findByUserId(foundUser.getId()));
            return foundUser;
        }
        throw new SQLException("User with email: {" + email + "} ,not found");
    }


    @Override
    public boolean isPhoneNumberExists(String phoneNumber) throws SQLException {
        log.info("Request to check if Phone Number exists for user: {}", phoneNumber);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        return user.isPresent();
    }

    @Override
    public User findById(String id) throws SQLException {
        log.info("Request to find User by id: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setOrders(orderRepository.findByUserId(foundUser.getId()));
            return foundUser;
        }
        throw new SQLException("User with id: {" + id + "} ,not found");
    }

    @Override
    public Collection<User> findAll() throws SQLException {
        log.info("Request to find all Users");
        return userRepository.findAll();
    }
}
