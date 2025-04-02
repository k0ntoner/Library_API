package org.example.configs.database;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class TestDatabaseConfig implements DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/library_test?ssl=false";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    private static final String CREATE_BOOKS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS books (\n" +
            "id BIGSERIAL PRIMARY KEY,\n" +
            "title VARCHAR(30) UNIQUE NOT NULL,\n" +
            "description VARCHAR(255),\n" +
            "author VARCHAR(30) NOT NULL\n" +
            ");";

    private static final String CREATE_BOOK_COPIES_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS book_copies (\n" +
            "id BIGSERIAL PRIMARY KEY,\n" +
            "book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,\n" +
            "status VARCHAR(10) CHECK (status IN ('AVAILABLE', 'BORROWED')) NOT NULL\n" +
            ");";

    private static final String CREATE_USERS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS users (\n" +
            "id VARCHAR(64) PRIMARY KEY,\n" +
            "first_name VARCHAR(30) NOT NULL,\n" +
            "last_name VARCHAR(30) NOT NULL,\n" +
            "email VARCHAR(100) UNIQUE NOT NULL,\n" +
            "phone_number VARCHAR(14) UNIQUE \n" +
            ");";

    private static final String CREATE_CUSTOMERS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS customers (\n" +
            "user_id VARCHAR(64) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,\n" +
            "date_of_birth DATE NOT NULL\n" +
            ");";

    private static final String CREATE_EMPLOYEES_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS employees (\n" +
            "user_id VARCHAR(64) PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,\n" +
            "salary DECIMAL(10,2) NOT NULL\n" +
            ");";

    private static final String CREATE_ORDERS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS orders (\n" +
            "id BIGSERIAL PRIMARY KEY,\n" +
            "user_id VARCHAR(64) NOT NULL REFERENCES users(id) ON DELETE CASCADE,\n" +
            "copy_id BIGINT NOT NULL REFERENCES book_copies(id) ON DELETE CASCADE,\n" +
            "subscription_type VARCHAR(15) CHECK (subscription_type IN ('READING_ROOM', 'SUBSCRIPTION')) NOT NULL,\n" +
            "order_date DATE NOT NULL,\n" +
            "expiration_date DATE NOT NULL,\n" +
            "status VARCHAR(10) CHECK (status IN ('BORROWED', 'RETURNED', 'OVERDUE')) NOT NULL\n" +
            ");";
    private static final String DROP_ORDERS_TABLE_QUERY = "DROP TABLE IF EXISTS orders CASCADE;";
    private static final String DROP_BOOK_COPIES_TABLE_QUERY = "DROP TABLE IF EXISTS book_copies CASCADE;";
    private static final String DROP_BOOKS_TABLE_QUERY = "DROP TABLE IF EXISTS books CASCADE;";
    private static final String DROP_EMPLOYEES_TABLE_QUERY = "DROP TABLE IF EXISTS employees CASCADE;";
    private static final String DROP_CUSTOMERS_TABLE_QUERY = "DROP TABLE IF EXISTS customers CASCADE;";
    private static final String DROP_USERS_TABLE_QUERY = "DROP TABLE IF EXISTS users CASCADE;";

    static {
        try {
            Class.forName("org.postgresql.Driver");
            dropTables();
            createTables();
            log.info("Database connection established...");
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to initialize test database connection", e);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to initialize test database connection", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void dropTables() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(DROP_ORDERS_TABLE_QUERY);
            statement.executeUpdate(DROP_BOOKS_TABLE_QUERY);
            statement.executeUpdate(DROP_BOOK_COPIES_TABLE_QUERY);
            statement.executeUpdate(DROP_CUSTOMERS_TABLE_QUERY);
            statement.executeUpdate(DROP_EMPLOYEES_TABLE_QUERY);
            statement.executeUpdate(DROP_USERS_TABLE_QUERY);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public static void createTables() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);) {
            Statement statement = connection.createStatement();
            statement.executeUpdate(CREATE_BOOKS_TABLE_QUERY);
            statement.executeUpdate(CREATE_BOOK_COPIES_TABLE_QUERY);
            statement.executeUpdate(CREATE_USERS_TABLE_QUERY);
            statement.executeUpdate(CREATE_CUSTOMERS_TABLE_QUERY);
            statement.executeUpdate(CREATE_EMPLOYEES_TABLE_QUERY);
            statement.executeUpdate(CREATE_ORDERS_TABLE_QUERY);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
