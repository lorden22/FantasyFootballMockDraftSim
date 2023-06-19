package com.example.Mock.DAO;

import com.example.Mock.DAO.UserDAO;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
    

@Repository
@Scope(value = "prototype")
public class UserDataObject implements UserDAO {

    private String username;
    private String hashPassword;
    private String salt;

    public UserDataObject(String username, String passwordText) throws NoSuchAlgorithmException {
        this.username = username;

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

    public boolean authenticateUser(String attemptedPassword) throws NoSuchAlgorithmException {
        MessageDigest digest;
        digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(this.salt.getBytes());
        byte[] hash = digest.digest(attemptedPassword.getBytes());
        String attemptedHashPassword = bypeArrayToString(hash);
        return attemptedHashPassword.equals(this.hashPassword);
    }

    public String getUsername() {
        return this.username;
    }

    private static String bypeArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}