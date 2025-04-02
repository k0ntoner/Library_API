package org.example.repositories.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.database.JDBCManager;
import org.example.entities.*;
import org.example.enums.Status;
import org.example.enums.SubscriptionType;
import org.example.repositories.CustomerRepository;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
public class CustomerRepositoryImpl implements CustomerRepository {
    private JDBCManager jdbcManager;

    public CustomerRepositoryImpl(JDBCManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }

    @Override
    public Customer save(Customer entity) throws SQLException {
        log.info("Request to save customer: {} into database", entity);

        String userQuery = "INSERT INTO users (id, first_name, last_name, email, phone_number) VALUES (?, ?, ?, ?, ?) RETURNING id, first_name, last_name, email, phone_number;";
        String customerQuery = "INSERT INTO customers (user_id, date_of_birth) VALUES (?, ?) RETURNING user_id, date_of_birth;";

        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(userQuery);
            preparedStatement.setString(1, entity.getId());
            preparedStatement.setString(2, entity.getFirstName());
            preparedStatement.setString(3, entity.getLastName());
            preparedStatement.setString(4, entity.getEmail());
            preparedStatement.setString(5, entity.getPhoneNumber());

            try (ResultSet userResultSet = preparedStatement.executeQuery()) {
                if (userResultSet.next()) {
                    Customer savedCustomer = Customer.builder()
                            .id(userResultSet.getString("id"))
                            .firstName(userResultSet.getString("first_name"))
                            .lastName(userResultSet.getString("last_name"))
                            .email(userResultSet.getString("email"))
                            .phoneNumber(userResultSet.getString("phone_number"))
                            .build();
                    preparedStatement = connection.prepareStatement(customerQuery);

                    preparedStatement.setString(1, savedCustomer.getId());
                    preparedStatement.setDate(2, Date.valueOf(entity.getDateOfBirth()));

                    try (ResultSet customerResultSet = preparedStatement.executeQuery()) {
                        if (customerResultSet.next()) {
                            savedCustomer.setDateOfBirth(customerResultSet.getDate("date_of_birth").toLocalDate());
                            connection.commit();
                            return savedCustomer;
                        }
                        throw new SQLException("Error while saving customer with email: {" + entity.getEmail() + "} into customers table");

                    } catch (SQLException e) {
                        throw new SQLException("Error while saving customer with email: {" + entity.getEmail() + "} into customers table", e);
                    }
                }
                throw new SQLException("Error while saving user with username: {" + entity.getId() + "} into users table");
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Customer> findById(String id) throws SQLException {
        log.info("Request to find customer with id: {" + id + "} from database...");
        String query = "SELECT * FROM users " +
                "INNER JOIN customers ON customers.user_id = users.id " +
                " WHERE customers.user_id = ? ";


        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapToCustomer(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public Customer update(Customer entity) throws SQLException {
        log.info("Request to update customer: {} into database", entity);

        String userQuery = "UPDATE users SET first_name = ?, last_name = ?,email = ?, phone_number=? WHERE id = ? RETURNING id, first_name, last_name, email, phone_number;";
        String customerQuery = "UPDATE  customers SET  date_of_birth = ? WHERE user_id = ? RETURNING user_id, date_of_birth;";

        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(userQuery);

            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getEmail());
            preparedStatement.setString(4, entity.getPhoneNumber());
            preparedStatement.setString(5, entity.getId());

            try (ResultSet userResultSet = preparedStatement.executeQuery()) {
                if (userResultSet.next()) {
                    Customer updatedCustomer = Customer.builder()
                            .id(userResultSet.getString("id"))
                            .firstName(userResultSet.getString("first_name"))
                            .lastName(userResultSet.getString("last_name"))
                            .email(userResultSet.getString("email"))
                            .phoneNumber(userResultSet.getString("phone_number"))
                            .build();
                    preparedStatement = connection.prepareStatement(customerQuery);


                    preparedStatement.setDate(1, Date.valueOf(entity.getDateOfBirth()));
                    preparedStatement.setString(2, updatedCustomer.getId());

                    try (ResultSet customerResultSet = preparedStatement.executeQuery()) {
                        if (customerResultSet.next()) {
                            updatedCustomer.setDateOfBirth(customerResultSet.getDate("date_of_birth").toLocalDate());
                            connection.commit();
                            return updatedCustomer;
                        }
                        throw new SQLException("Error while updating customer with email: {" + entity.getEmail() + "} into customers table");

                    } catch (SQLException e) {
                        throw new SQLException("Error while updating customer with email: {" + entity.getEmail() + "} into customers table", e);
                    }
                }
                throw new SQLException("Error while updating user with username: {" + entity.getId() + "} into users table");
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(Customer entity) throws SQLException {
        log.info("Request to delete customer: {} from database", entity);

        String userQuery = "Delete from users where id = ?";
        String customerQuery = "Delete from customers where user_id = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(customerQuery);

            preparedStatement.setString(1, entity.getId());

            try {
                preparedStatement.execute();

                preparedStatement = connection.prepareStatement(userQuery);

                preparedStatement.setString(1, entity.getId());

                preparedStatement.execute();

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Collection<Customer> findAll() throws SQLException {
        log.info("Request to find all customers from database...");
        String query = "SELECT * FROM users " +
                "INNER JOIN customers ON customers.user_id = users.id ";


        try (Connection connection = jdbcManager.getConnection()) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            Collection<Customer> customers = new ArrayList<>();
            while (resultSet.next()) {
                Customer customer = mapToCustomer(resultSet);
                customers.add(customer);
            }
            return customers;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public Collection<Order> findAllOrdersByEmail(String email) throws SQLException {
        return List.of();
    }

    private Customer mapToCustomer(ResultSet result) throws SQLException {
        return Customer.builder()
                .id(result.getString("id"))
                .email(result.getString("email"))
                .firstName(result.getString("first_name"))
                .lastName(result.getString("last_name"))
                .phoneNumber(result.getString("phone_number"))
                .dateOfBirth(result.getDate("date_of_birth").toLocalDate())
                .build();
    }

}
