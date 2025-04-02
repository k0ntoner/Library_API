package org.example.repositories.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.database.JDBCManager;
import org.example.entities.Employee;
import org.example.repositories.EmployeeRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private JDBCManager jdbcManager;

    public EmployeeRepositoryImpl(JDBCManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }

    @Override
    public Employee save(Employee entity) throws SQLException {
        log.info("Request to save customer: {} into database", entity);

        String userQuery = "INSERT INTO users (id, first_name, last_name, email, phone_number) VALUES (?, ?, ?, ?, ?) RETURNING id, first_name, last_name, email, phone_number;";
        String employeeQuery = "INSERT INTO employees (user_id, salary) VALUES (?, ?) RETURNING user_id, salary;";

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
                    Employee savedEmployee = Employee.builder()
                            .id(userResultSet.getString("id"))
                            .firstName(userResultSet.getString("first_name"))
                            .lastName(userResultSet.getString("last_name"))
                            .email(userResultSet.getString("email"))
                            .phoneNumber(userResultSet.getString("phone_number"))
                            .build();
                    preparedStatement = connection.prepareStatement(employeeQuery);

                    preparedStatement.setString(1, savedEmployee.getId());
                    preparedStatement.setBigDecimal(2, entity.getSalary());

                    try (ResultSet employeeResultSet = preparedStatement.executeQuery()) {
                        if (employeeResultSet.next()) {
                            savedEmployee.setSalary(employeeResultSet.getBigDecimal("salary"));
                            connection.commit();
                            return savedEmployee;
                        }
                        throw new SQLException("Error while saving employee with email: {" + entity.getEmail() + "} into employees table");

                    } catch (SQLException e) {
                        throw new SQLException("Error while saving employee with email: {" + entity.getEmail() + "} into employees table", e);
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
    public Optional<Employee> findById(String id) throws SQLException {
        log.info("Request to find employee with id: {" + id + "} from database...");
        String query = "SELECT * FROM users " +
                "INNER JOIN employees ON employees.user_id = users.id " +
                " WHERE employees.user_id = ? ";


        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapToEmployee(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public Employee update(Employee entity) throws SQLException {
        log.info("Request to update employee: {} into database", entity);

        String userQuery = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone_number=? WHERE id = ? RETURNING id, first_name, last_name, email, phone_number;";
        String employeeQuery = "UPDATE  employees SET  salary = ? WHERE user_id = ? RETURNING user_id, salary;";

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
                    Employee updatedEmployee = Employee.builder()
                            .id(userResultSet.getString("id"))
                            .firstName(userResultSet.getString("first_name"))
                            .lastName(userResultSet.getString("last_name"))
                            .email(userResultSet.getString("email"))
                            .phoneNumber(userResultSet.getString("phone_number"))
                            .build();
                    preparedStatement = connection.prepareStatement(employeeQuery);


                    preparedStatement.setBigDecimal(1, entity.getSalary());
                    preparedStatement.setString(2, updatedEmployee.getId());

                    try (ResultSet customerResultSet = preparedStatement.executeQuery()) {
                        if (customerResultSet.next()) {
                            updatedEmployee.setSalary(customerResultSet.getBigDecimal("salary"));
                            connection.commit();
                            return updatedEmployee;
                        }
                        throw new SQLException("Error while updating employee with email: {" + entity.getEmail() + "} into employees table");

                    } catch (SQLException e) {
                        throw new SQLException("Error while updating employee with email: {" + entity.getEmail() + "} into employees table", e);
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
    public void delete(Employee entity) throws SQLException {
        log.info("Request to delete employee: {} from database", entity);

        String userQuery = "Delete from users where id = ?";
        String employeeQuery = "Delete from employees where user_id = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(employeeQuery);

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
    public Collection<Employee> findAll() throws SQLException {
        log.info("Request to find all customers from database...");
        String query = "SELECT * FROM users " +
                "INNER JOIN employees ON employees.user_id = users.id ";


        try (Connection connection = jdbcManager.getConnection()) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            Collection<Employee> employees = new ArrayList<>();
            while (resultSet.next()) {
                Employee employee = mapToEmployee(resultSet);
                employees.add(employee);
            }
            return employees;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private Employee mapToEmployee(ResultSet result) throws SQLException {
        return Employee.builder()
                .id(result.getString("id"))
                .email(result.getString("email"))
                .firstName(result.getString("first_name"))
                .lastName(result.getString("last_name"))
                .phoneNumber(result.getString("phone_number"))
                .salary(result.getBigDecimal("salary"))
                .build();
    }
}
