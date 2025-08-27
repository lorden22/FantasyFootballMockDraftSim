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
import com.example.common.Logger;

@Service
public class LoginServices{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public boolean checkUser(String username) {
        Logger.logAuth(username, "CHECK_USER", "ATTEMPT");
        String sql = "SELECT * FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        
        ArrayList<Map<String,Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        boolean userExists = returnList.size() != 0;
        Logger.logAuth(username, "CHECK_USER", userExists ? "USER_FOUND" : "USER_NOT_FOUND");
        return userExists;
    }

    public boolean addUser(String usernameWanted, String passwordWanted) throws NoSuchAlgorithmException {
        Logger.logAuth(usernameWanted, "ADD_USER", "ATTEMPT");
        if(checkUser(usernameWanted)) {
            Logger.logAuth(usernameWanted, "ADD_USER", "FAILED_USER_EXISTS");
            return false;
        }
        UserDataObject userToAdd = new UserDataObject(usernameWanted, passwordWanted, this.namedParameterJdbcTemplate);
        boolean success = userToAdd.addUserToDatabase();
        Logger.logAuth(usernameWanted, "ADD_USER", success ? "SUCCESS" : "FAILED");
        return success;
    }

    public boolean removeUser(String username) {
        Logger.logAuth(username, "REMOVE_USER", "ATTEMPT");
        if(!checkUser(username)) {
            Logger.logAuth(username, "REMOVE_USER", "FAILED_USER_NOT_EXISTS");
            return false;
        }
        String sql = "DELETE FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        namedParameterJdbcTemplate.update(sql, params);
        Logger.logAuth(username, "REMOVE_USER", "SUCCESS");
        return true;
    }

    public boolean authenticateUserPassword(String username, String password) throws NoSuchAlgorithmException {
        Logger.logAuth(username, "AUTH_PASSWORD", "ATTEMPT");
        if(!checkUser(username)) {
            Logger.logAuth(username, "AUTH_PASSWORD", "FAILED_USER_NOT_EXISTS");
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
        boolean authenticated = attemptedHashPassword.equals(hashPassword);
        Logger.logAuth(username, "AUTH_PASSWORD", authenticated ? "SUCCESS" : "FAILED_WRONG_PASSWORD");
        return authenticated;
    }

    public boolean authenticateUserSessionID(String username, String sessionID) throws NoSuchAlgorithmException {
        Logger.logAuth(username, "AUTH_SESSION", "ATTEMPT");
        if(!checkUser(username)) {
            Logger.logAuth(username, "AUTH_SESSION", "FAILED_USER_NOT_EXISTS");
            return false;
        }

        String sql = "SELECT * FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        ArrayList<Map<String,Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        String latestSessionID = returnList.get(0).get("recent_session_id").toString();
        boolean authenticated = latestSessionID.equals(sessionID);
        Logger.logAuth(username, "AUTH_SESSION", authenticated ? "SUCCESS" : "FAILED_INVALID_SESSION");
        return authenticated;
    }

    public String generateSessionID(String username) throws NoSuchAlgorithmException {
        Logger.logAuth(username, "GENERATE_SESSION", "ATTEMPT");
        if(!checkUser(username)) {
            Logger.logAuth(username, "GENERATE_SESSION", "FAILED_USER_NOT_EXISTS");
            return null;
        }
        String sessionID = this.generateAUserSessionID();
        Logger.logAuth(username, "GENERATE_SESSION", "SUCCESS_" + sessionID);

        String sql = "UPDATE users SET recent_session_id = :sessionID WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sessionID", sessionID);
        params.addValue("username", username);

        namedParameterJdbcTemplate.update(sql, params);
        return sessionID;
    }

    public boolean logout(String username, String sessionID) throws NoSuchAlgorithmException {
        Logger.logAuth(username, "LOGOUT", "ATTEMPT");
        if(!this.authenticateUserSessionID(username, sessionID)) {
            Logger.logAuth(username, "LOGOUT", "FAILED_NOT_AUTHENTICATED");
            return false;
        }

        String sql = "UPDATE users SET recent_session_id = NULL WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        namedParameterJdbcTemplate.update(sql, params);
        Logger.logAuth(username, "LOGOUT", "SUCCESS");
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
