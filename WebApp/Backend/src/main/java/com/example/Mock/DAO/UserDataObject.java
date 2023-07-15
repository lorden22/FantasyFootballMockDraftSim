package com.example.Mock.DAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.example.Mock.DAO.UserDAO;
    

@Repository
@Scope(value = "prototype")
public class UserDataObject implements UserDAO  {

    private String username;
    private String hashPassword;
    private String salt;
    private String latestSessionID;

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

    public boolean authenticateUserPassword(String attemptedPassword) throws NoSuchAlgorithmException {
        MessageDigest digest;
        digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(this.salt.getBytes());
        byte[] hash = digest.digest(attemptedPassword.getBytes());
        String attemptedHashPassword = bypeArrayToString(hash);
        System.out.println(attemptedHashPassword + " " + this.hashPassword);
        return attemptedHashPassword.equals(this.hashPassword);
    }

    public boolean authenticateSessionID(String attemptedSessionID) {
        return attemptedSessionID.equals(this.latestSessionID);
    }

    public String generateSessionID() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        this.latestSessionID = bypeArrayToString(bytes);
        return this.latestSessionID;
    }

    public String getUsername() {
        return this.username;
    }

     public String getSessionID() {
        return this.latestSessionID;
    }

    public boolean logout() {
        this.latestSessionID = null;
        return true;
    }

    private static String bypeArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}