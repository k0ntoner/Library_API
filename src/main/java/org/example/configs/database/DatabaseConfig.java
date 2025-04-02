package org.example.configs.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConfig {
    Connection getConnection() throws SQLException;
}
