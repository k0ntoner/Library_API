package org.example.repositories.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.database.JDBCManager;
import org.example.entities.*;
import org.example.enums.Status;
import org.example.enums.SubscriptionType;
import org.example.repositories.UserRepository;

import java.sql.*;
import java.util.*;

@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private JDBCManager jdbcManager;

    public UserRepositoryImpl(JDBCManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        log.info("Request to find user with email: {} from database", email);

        String query = "SELECT * FROM users " +
                "LEFT JOIN customers ON customers.user_id = users.id " +
                "LEFT JOIN employees ON employees.user_id = users.id " +
                "WHERE users.email = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapToUser(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new SQLException(e);
        }
    }


    @Override
    public Optional<User> findById(String id) throws SQLException {
        log.info("Request to find user with id: {} from database", id);

        String query = "SELECT * FROM users " +
                "LEFT JOIN customers ON customers.user_id = users.id " +
                "LEFT JOIN employees ON employees.user_id = users.id " +
                "WHERE users.id = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = mapToUser(resultSet);
                return Optional.of(user);
            }
            return Optional.empty();

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new SQLException(e);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) throws SQLException {
        log.info("Request to find user with phone nimber: {} from database", phoneNumber);

        String query = "SELECT * FROM users " +
                "LEFT JOIN customers ON customers.user_id = users.id " +
                "LEFT JOIN employees ON employees.user_id = users.id " +
                "WHERE users.phone_number = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, phoneNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapToUser(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new SQLException(e);
        }
    }


    @Override
    public boolean isUserHasOverdue(String id) throws SQLException {
        log.info("Request to overdue orders by id: {}", id);

        String query = "SELECT * FROM orders " +
                "INNER JOIN users ON orders.user_id = users.id " +
                "WHERE users.id = ? and status = ? ";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, Status.OVERDUE.toString().toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Collection<User> findAll() throws SQLException {
        log.info("Request to find all users from database...");
        String query = "SELECT * FROM users " +
                "LEFT JOIN customers ON customers.user_id = users.id " +
                "LEFT JOIN employees ON employees.user_id = users.id ";


        try (Connection connection = jdbcManager.getConnection()) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            Collection<User> users = new ArrayList<>();
            while (resultSet.next()) {
                User user = mapToUser(resultSet);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private User mapToUser(ResultSet result) throws SQLException {
        if (result.getDate("date_of_birth") != null) {
            return Customer.builder()
                    .id(result.getString("id"))
                    .email(result.getString("email"))
                    .firstName(result.getString("first_name"))
                    .lastName(result.getString("last_name"))
                    .phoneNumber(result.getString("phone_number"))
                    .dateOfBirth(result.getDate("date_of_birth").toLocalDate())
                    .build();
        } else if (result.getBigDecimal("salary") != null) {
            return Employee.builder()
                    .id(result.getString("id"))
                    .email(result.getString("email"))
                    .firstName(result.getString("first_name"))
                    .lastName(result.getString("last_name"))
                    .phoneNumber(result.getString("phone_number"))
                    .salary(result.getBigDecimal("salary"))
                    .build();
        }
        throw new NoSuchElementException("Such type of user is not exists");
    }
}
