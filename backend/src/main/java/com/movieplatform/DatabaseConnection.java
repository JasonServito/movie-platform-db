package com.movieplatform;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnection {
    // Connection settings come from environment variables (DB_PASSWORD is required)
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:oracle:thin:@localhost:1521/FREE");
    private static final String USERNAME = System.getenv().getOrDefault("DB_USER", "SYSTEM");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database: " + e.getMessage());
        }
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing the connection: " + e.getMessage());
        }
    }
}
