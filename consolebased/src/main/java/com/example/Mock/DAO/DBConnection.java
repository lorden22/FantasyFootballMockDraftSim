package com.example.Mock.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        // Try localhost first for embedded database, then external options
        String[] hostOptions = {"localhost", "mysql-console-db", "host.docker.internal"};
        String database = "db";
        String user = "root";
        String password = "password";
        
        SQLException lastException = null;
        
        for (String host : hostOptions) {
            try {
                String url = "jdbc:mysql://" + host + ":3306/" + database + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                Connection conn = DriverManager.getConnection(url, user, password);
                
                // Test the connection
                try (PreparedStatement testStmt = conn.prepareStatement("SELECT COUNT(*) FROM users")) {
                    try (ResultSet rs = testStmt.executeQuery()) {
                        if (rs.next()) {
                            // Connection successful, return it
                        }
                    }
                }
                return conn;
            } catch (SQLException e) {
                System.err.println("❌ FAILED connection to " + host + ": " + e.getMessage());
                lastException = e;
            }
        }
        
        System.err.println("❌ ALL CONNECTION ATTEMPTS FAILED");
        throw lastException != null ? lastException : new SQLException("No database connection could be established");
    }
} 