package com.example.Mock.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.Mock.DAO.UserDataObject;

@Service
public class LoginServices{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public boolean checkUser(String username) {
        System.out.println("Checking User - " + username);
        String sql = "SELECT * FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        
        ArrayList<Map<String,Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        System.out.println(returnList.size() + "- 0 is user not found, 1 is user found");
        return returnList.size() != 0;
    }

    public boolean addUser(String usernameWanted, String passwordWanted) throws NoSuchAlgorithmException {
        if(checkUser(usernameWanted)) {
            System.out.println("User Already Exists");
            return false;
        }
        System.out.println("Adding User");
        UserDataObject userToAdd = new UserDataObject(usernameWanted, passwordWanted, this.namedParameterJdbcTemplate);
        System.out.println("Adding user to db...\n" + userToAdd.addUserToDatabase());
        return true;
    }

    public boolean removeUser(String username) {
        if(!checkUser(username)) {
            System.out.println("User Does Not Exist");
            return false;
        }
        String sql = "DELETE FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        namedParameterJdbcTemplate.update(sql, params);
        return true;
    }

    public boolean authenticateUserPassword(String username, String password) throws NoSuchAlgorithmException {
        if(!checkUser(username)) {
            System.out.println("User Does Not Exist");
            return false;
        }

        String sql = "SELECT * FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        ArrayList<Map<String,Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        String salt = returnList.get(0).get("salt").toString();
        String hashPassword = returnList.get(0).get("hash_pass").toString();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(salt.getBytes());
        byte[] hash = digest.digest(password.getBytes());
        String attemptedHashPassword = bypeArrayToString(hash);
        System.out.println(attemptedHashPassword + " " + hashPassword);
        return attemptedHashPassword.equals(hashPassword);
    }

    public boolean authenticateUserSessionID(String username, String sessionID) throws NoSuchAlgorithmException {
        if(!checkUser(username)) {
            System.out.println("User Does Not Exist");
            return false;
        }

        String sql = "SELECT * FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        ArrayList<Map<String,Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        String latestSessionID = returnList.get(0).get("recent_session_id").toString();
        return latestSessionID.equals(sessionID);
    }

    public String generateSessionID(String username) throws NoSuchAlgorithmException {
        if(!checkUser(username)) {
            System.out.println("User Does Not Exist");
            return null;
        }
        System.out.println("Generating Session ID");
        String sessionID = this.generateAUserSessionID();
        System.out.println("Session ID Generated: " + sessionID);

        String sql = "UPDATE users SET recent_session_id = :sessionID WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sessionID", sessionID);
        params.addValue("username", username);

        namedParameterJdbcTemplate.update(sql, params);
        return sessionID;
    }

    public boolean logout(String username, String sessionID) throws NoSuchAlgorithmException {
        if(!this.authenticateUserSessionID(username, sessionID)) {
            System.out.println("User Not Authenticated and Could Not Be Logout");
            return false;
        }

        String sql = "UPDATE users SET recent_session_id = NULL WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        namedParameterJdbcTemplate.update(sql, params);
        return true;
    }

    private static String bypeArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String generateAUserSessionID() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return bypeArrayToString(bytes);
    }
}
