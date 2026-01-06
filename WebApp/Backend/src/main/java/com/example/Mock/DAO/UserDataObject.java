package com.example.Mock.DAO;

import java.security.NoSuchAlgorithmException;

import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.common.Logger;

@Repository
@Scope(value = "prototype")
public class UserDataObject implements UserDAO {

    private String username;
    private String hashPassword;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserDataObject(String username, String passwordText, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws NoSuchAlgorithmException {
        this.username = username;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        // Use BCrypt for secure password hashing (includes salt automatically)
        this.hashPassword = passwordEncoder.encode(passwordText);
    }

    public boolean addUserToDatabase() {
        // Store BCrypt hash in hash_pass column, salt column stores "BCRYPT" marker
        String sql = "INSERT INTO users (username, hash_pass, salt) VALUES (:username, :hash_pass, :salt)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", this.username);
        params.addValue("hash_pass", this.hashPassword);
        params.addValue("salt", "BCRYPT"); // Marker to indicate BCrypt hash

        try {
            namedParameterJdbcTemplate.update(sql, params);
            Logger.logAuth(this.username, "USER_CREATED", "SUCCESS");
            return true;
        } catch (Exception error) {
            Logger.logError("Failed to add user " + this.username + " to database: " + error.getMessage());
            return false;
        }
    }

    public static BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
