package com.example.Mock.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.example.common.Logger;
import com.example.Mock.DAO.UserDataObject;
import com.example.Mock.DAO.DBConnection;

public class LoginServices {
    public static boolean checkUser(String username) {
        Logger.logAuth(username, "CHECK_USER", "ATTEMPT");
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                boolean userExists = rs.getInt(1) > 0;
                Logger.logAuth(username, "CHECK_USER", userExists ? "USER_FOUND" : "USER_NOT_FOUND");
                return userExists;
            }
        } catch (SQLException e) {
            Logger.logError("Database error checking user " + username + ": " + e.getMessage());
        }
        Logger.logAuth(username, "CHECK_USER", "ERROR");
        return false;
    }

    public static boolean authenticateUserPassword(String username, String password) {
        Logger.logAuth(username, "AUTH_PASSWORD", "ATTEMPT");
        String sql = "SELECT hash_pass, salt FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashPassword = rs.getString("hash_pass");
                String salt = rs.getString("salt");
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.reset();
                digest.update(salt.getBytes());
                byte[] hash = digest.digest(password.getBytes());
                String attemptedHashPassword = byteArrayToString(hash);
                boolean authenticated = attemptedHashPassword.equals(hashPassword);
                Logger.logAuth(username, "AUTH_PASSWORD", authenticated ? "SUCCESS" : "FAILED_WRONG_PASSWORD");
                return authenticated;
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            Logger.logError("Authentication error for user " + username + ": " + e.getMessage());
        }
        Logger.logAuth(username, "AUTH_PASSWORD", "ERROR");
        return false;
    }

    private static String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
} 