package org.example.configs.database;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCManager {
    private DatabaseConfig config;

    public JDBCManager(DatabaseConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        return config.getConnection();
    }
}
