package com.example.Mock.DAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.example.common.Logger;

public class UserDataObject implements UserDAO  {
    private String username;
    private String hashPassword;
    private String salt;

    public UserDataObject(String username, String passwordText) throws NoSuchAlgorithmException {
        this.username = username;
        Logger.logAuth(username, "USER_OBJECT_CREATED", "Creating user object with hashed password");
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        this.salt = bypeArrayToString(bytes);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(this.salt.getBytes());
        byte[] hash = digest.digest(passwordText.getBytes());
        this.hashPassword = bypeArrayToString(hash);
    }

    public boolean addUserToDatabase() {
        Logger.logAuth(this.username, "ADD_USER_TO_DB", "ATTEMPT");
        String sql = "INSERT INTO users (username, hash_pass, salt) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.username);
            stmt.setString(2, this.hashPassword);
            stmt.setString(3, this.salt);
            stmt.executeUpdate();
            Logger.logAuth(this.username, "ADD_USER_TO_DB", "SUCCESS");
            return true;
        } catch (SQLException error) {
            Logger.logError("Failed to add user " + this.username + " to database: " + error.getMessage());
            Logger.logAuth(this.username, "ADD_USER_TO_DB", "FAILED");
            return false;
        }
    }

    private static String bypeArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
} 