package org.example.repositories.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.database.JDBCManager;
import org.example.entities.*;
import org.example.enums.Status;
import org.example.enums.SubscriptionType;
import org.example.repositories.BookCopyRepository;
import org.example.repositories.OrderRepository;
import org.example.repositories.UserRepository;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
public class OrderRepositoryImpl implements OrderRepository {
    private JDBCManager jdbcManager;

    private UserRepository userRepository;

    private BookCopyRepository bookCopyRepository;

    public OrderRepositoryImpl(JDBCManager jdbcManager, UserRepository userRepository, BookCopyRepository bookCopyRepository) {
        this.jdbcManager = jdbcManager;
        this.userRepository = userRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Override
    public Collection<Order> findByUserId(String user_id) throws SQLException {
        log.info("Request to find Book Copies by username: {}", user_id);

        String query = "SELECT * FROM orders " +
                "INNER JOIN users ON orders.user_id = users.id " +
                "WHERE users.id = ? ";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user_id);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<Order> foundOrders = new ArrayList<>();
            while (resultSet.next()) {
                Order order = Order.builder()
                        .id(resultSet.getLong("id"))
                        .subscriptionType(SubscriptionType.valueOf(resultSet.getString("subscription_type")))
                        .orderDate(resultSet.getDate("order_date").toLocalDate())
                        .expirationDate(resultSet.getDate("expiration_date").toLocalDate())
                        .status(Status.valueOf(resultSet.getString("status")))
                        .build();

                String userId = resultSet.getString("user_id");
                Long copyId = resultSet.getLong("copy_id");
                userRepository.findById(userId).ifPresentOrElse(order::setUser, () -> {
                    throw new NoSuchElementException("User with id: " + userId + " not found");
                });
                bookCopyRepository.findById(copyId).ifPresentOrElse(order::setBookCopy, () -> {
                    throw new NoSuchElementException("Copy of book with id: " + copyId + " not found");
                });

                foundOrders.add(order);
            }
            return foundOrders;
        } catch (SQLException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Order save(Order entity) throws SQLException {
        log.info("Request to save order: {} into database", entity);

        String order_query = "INSERT INTO orders (user_id, copy_id, subscription_type, order_date, expiration_date, status) VALUES (?, ?, ?, ?, ?, ?) RETURNING id, user_id, copy_id, subscription_type, order_date, expiration_date, status;";
        String copy_query = "Update book_copies set status = ? where id = ? RETURNING id";

        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(order_query);

            preparedStatement.setString(1, entity.getUser().getId());
            preparedStatement.setLong(2, entity.getBookCopy().getId());
            preparedStatement.setString(3, entity.getSubscriptionType().toString().toUpperCase());
            preparedStatement.setDate(4, Date.valueOf(entity.getOrderDate()));
            preparedStatement.setDate(5, Date.valueOf(entity.getExpirationDate()));
            preparedStatement.setString(6, entity.getStatus().toString().toUpperCase());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Order savedOrder = Order.builder()
                            .id(resultSet.getLong("id"))
                            .subscriptionType(SubscriptionType.valueOf(resultSet.getString("subscription_type")))
                            .orderDate(resultSet.getDate("order_date").toLocalDate())
                            .expirationDate(resultSet.getDate("expiration_date").toLocalDate())
                            .status(Status.valueOf(resultSet.getString("status")))
                            .build();

                    preparedStatement = connection.prepareStatement(copy_query);

                    preparedStatement.setString(1, Status.BORROWED.toString().toUpperCase());
                    preparedStatement.setLong(2, entity.getBookCopy().getId());
                    try (ResultSet copyResultSet = preparedStatement.executeQuery()) {
                        if (!copyResultSet.next()) {
                            throw new SQLException("Error while updating book copy with id: " + entity.getBookCopy().getId());
                        }
                        connection.commit();
                    }

                    String userId = resultSet.getString("user_id");
                    Long copyId = resultSet.getLong("copy_id");
                    userRepository.findById(userId).ifPresentOrElse(savedOrder::setUser, () -> {
                        throw new NoSuchElementException("User with id: " + userId + " not found");
                    });
                    bookCopyRepository.findById(copyId).ifPresentOrElse(savedOrder::setBookCopy, () -> {
                        throw new NoSuchElementException("Copy of book with id: " + copyId + " not found");
                    });


                    return savedOrder;
                }
                throw new SQLException("Error while saving order into orders table");
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
    public Optional<Order> findById(Long id) throws SQLException {
        log.info("Request to find Order with id: {} from database", id);

        String query = "SELECT * FROM orders " +
                " WHERE orders.id = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Order foundOrder = Order.builder()
                        .id(resultSet.getLong("id"))
                        .subscriptionType(SubscriptionType.valueOf(resultSet.getString("subscription_type")))
                        .orderDate(resultSet.getDate("order_date").toLocalDate())
                        .expirationDate(resultSet.getDate("expiration_date").toLocalDate())
                        .status(Status.valueOf(resultSet.getString("status")))
                        .build();

                String userId = resultSet.getString("user_id");
                Long copyId = resultSet.getLong("copy_id");
                userRepository.findById(userId).ifPresentOrElse(foundOrder::setUser, () -> {
                    throw new NoSuchElementException("User with id: " + userId + " not found");
                });
                bookCopyRepository.findById(copyId).ifPresentOrElse(foundOrder::setBookCopy, () -> {
                    throw new NoSuchElementException("Copy of book with id: " + copyId + " not found");
                });
                return Optional.of(foundOrder);
            }
            return Optional.empty();

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new SQLException(e);
        }
    }

    @Override
    public Order update(Order entity) throws SQLException {
        log.info("Request to update order: {} into database", entity);

        String order_query = "UPDATE orders SET subscription_type = ?, order_date = ?, expiration_date = ?, status = ? WHERE id = ? RETURNING id, user_id, copy_id, subscription_type, order_date, expiration_date, status;";
        String copy_query = "Update book_copies SET status = ? where id = ? RETURNING id";

        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(order_query);

            preparedStatement.setString(1, entity.getSubscriptionType().toString());
            preparedStatement.setDate(2, Date.valueOf(entity.getOrderDate()));
            preparedStatement.setDate(3, Date.valueOf(entity.getExpirationDate()));
            preparedStatement.setString(4, entity.getStatus().toString().toUpperCase());
            preparedStatement.setLong(5, entity.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Order updatedOrder = Order.builder()
                            .id(resultSet.getLong("id"))
                            .subscriptionType(SubscriptionType.valueOf(resultSet.getString("subscription_type")))
                            .orderDate(resultSet.getDate("order_date").toLocalDate())
                            .expirationDate(resultSet.getDate("expiration_date").toLocalDate())
                            .status(Status.valueOf(resultSet.getString("status")))
                            .build();

                    preparedStatement = connection.prepareStatement(copy_query);

                    if (Status.RETURNED.equals(updatedOrder.getStatus())) {
                        preparedStatement.setString(1, Status.AVAILABLE.toString().toUpperCase());
                    } else if (Status.BORROWED.equals(updatedOrder.getStatus()) || Status.OVERDUE.equals(updatedOrder.getStatus())) {
                        preparedStatement.setString(1, Status.BORROWED.toString().toUpperCase());
                    }


                    preparedStatement.setLong(2, entity.getBookCopy().getId());
                    try (ResultSet copyResultSet = preparedStatement.executeQuery()) {
                        if (!copyResultSet.next()) {
                            throw new SQLException("Error while updating book copy with id: " + entity.getBookCopy().getId());
                        }
                        connection.commit();
                    }

                    String userId = resultSet.getString("user_id");
                    Long copyId = resultSet.getLong("copy_id");
                    userRepository.findById(userId).ifPresentOrElse(updatedOrder::setUser, () -> {
                        throw new NoSuchElementException("User with id: " + userId + " not found");
                    });
                    bookCopyRepository.findById(copyId).ifPresentOrElse(updatedOrder::setBookCopy, () -> {
                        throw new NoSuchElementException("Copy of book with id: " + copyId + " not found");
                    });
                    return updatedOrder;
                }
                throw new SQLException("Error while saving BookCopy with id: " + entity.getId());
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
    public void delete(Order entity) throws SQLException {
        log.info("Request to delete order with id: {" + entity.getId() + "} from database...");

        String query = "DELETE FROM orders WHERE id = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setLong(1, entity.getId());
            try {
                statement.execute();
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
    public Collection<Order> findAll() throws SQLException {
        log.info("Request to find Orders  from database");

        String query = "SELECT * FROM orders ";

        try (Connection connection = jdbcManager.getConnection()) {
            Statement statement = connection.createStatement();


            ResultSet resultSet = statement.executeQuery(query);

            Collection<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                Order foundOrder = Order.builder()
                        .id(resultSet.getLong("id"))
                        .subscriptionType(SubscriptionType.valueOf(resultSet.getString("subscription_type")))
                        .orderDate(resultSet.getDate("order_date").toLocalDate())
                        .expirationDate(resultSet.getDate("expiration_date").toLocalDate())
                        .status(Status.valueOf(resultSet.getString("status")))
                        .build();

                String userId = resultSet.getString("user_id");
                Long copyId = resultSet.getLong("copy_id");
                userRepository.findById(userId).ifPresentOrElse(foundOrder::setUser, () -> {
                    throw new NoSuchElementException("User with id: " + userId + " not found");
                });
                bookCopyRepository.findById(copyId).ifPresentOrElse(foundOrder::setBookCopy, () -> {
                    throw new NoSuchElementException("Copy of book with id: " + copyId + " not found");
                });

                orders.add(foundOrder);
            }
            return orders;

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new SQLException(e);
        }
    }


}
