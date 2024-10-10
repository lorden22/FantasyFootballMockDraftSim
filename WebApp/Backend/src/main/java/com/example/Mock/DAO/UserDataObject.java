package com.example.Mock.DAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value = "prototype")
public class UserDataObject implements UserDAO  {

    private String username;
    private String hashPassword;
    private String salt;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserDataObject(String username, String passwordText, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws NoSuchAlgorithmException {
        this.username = username;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        this.salt = bypeArrayToString(bytes);
        System.out.println(this.salt);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(this.salt.getBytes());
        byte[] hash = digest.digest(passwordText.getBytes());
        this.hashPassword = bypeArrayToString(hash);
    }

    public boolean addUserToDatabase() {
        String sql = "INSERT INTO users (username, hash_pass, salt) VALUES (:username, :hash_pass, :salt)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", this.username);
        params.addValue("hash_pass", this.hashPassword);
        params.addValue("salt", this.salt);
        
        try {
            namedParameterJdbcTemplate.update(sql, params);
            return true;
        } catch (Exception error) {
            error.printStackTrace();
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
