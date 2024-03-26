package com.example.Mock.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.Mock.DAO.UserDataObject;

@Service
public class LoginServices{

    //private HashSet<UserDataObject> users;

    public LoginServices() {
        //this.users = new HashSet<UserDataObject>();
    }

    public boolean checkUser(String username, JdbcTemplate jdbcTemplate) {
        System.out.println("Checking User - " + username);
        ArrayList<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>(jdbcTemplate.queryForList("SELECT * FROM users WHERE username = '" + username + "'"));
        System.out.println(returnList.size() + "- 0 is user not found, 1 is user found");
        if(returnList.size() == 0) {
            System.out.println("False");
            return false;
        }
        else {
            System.out.println("True");
            return true;
        }

        /*for(UserDataObject user : users) {
            if(user.getUsername().equals(username)) {
                System.out.println("True");
                return true;
            }
        }
        System.out.println("False");
        return false;
        */

    }

    public boolean addUser(String usernameWanted, String passwordWanted, JdbcTemplate jdbcTemplate) throws NoSuchAlgorithmException {
        if(checkUser(usernameWanted,jdbcTemplate) == true) {
            System.out.println("User Already Exists");
            return false;
        }
        System.out.println("Adding User");
        UserDataObject userToAdd = new UserDataObject(usernameWanted, passwordWanted);
        //users.add(userToAdd);
        System.out.println("Adding user to db...\n" + userToAdd.addUserToDatabase(jdbcTemplate));
        return true;
    }

    public boolean removeUser(String username, JdbcTemplate jdbcTemplate) {
        if(checkUser(username,jdbcTemplate) == false) {
            System.out.println("User Does Not Exist");
            return false;
        }
        jdbcTemplate.execute("DELETE FROM users WHERE username = '" + username + "'");
        return true;
        

        /*for(UserDataObject user : users) {
            if(user.getUsername().equals(username)) {
                users.remove(user);
                return true;
            }
        }
        return false;*/
    }

    public boolean authenticateUserPassword(String username, String password, JdbcTemplate jdbcTemplate) throws NoSuchAlgorithmException {
        if(checkUser(username,jdbcTemplate) == false) {
            System.out.println("User Does Not Exist");
            return false;
        }

        ArrayList<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>(jdbcTemplate.queryForList("SELECT * FROM users WHERE username = '" + username + "'"));
        String salt = returnList.get(0).get("salt").toString();
        String hashPassword = returnList.get(0).get("hash_pass").toString();
        MessageDigest digest;
        digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(salt.getBytes());
        byte[] hash = digest.digest(password.getBytes());
        String attemptedHashPassword = bypeArrayToString(hash);
        System.out.println(attemptedHashPassword + " " + hashPassword);
        return attemptedHashPassword.equals(hashPassword);

        /*for(UserDataObject user : users) {
            if(user.getUsername().equals(username) && user.authenticateUserPassword(password)) {
                return true;
            }
        }
        return false;*/
    }

    public boolean authenticateUserSessionID(String username, String sessionID, JdbcTemplate jdbcTemplate) throws NoSuchAlgorithmException {
        if(checkUser(username,jdbcTemplate) == false) {
            System.out.println("User Does Not Exist");
            return false;
        }
        ArrayList<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>(jdbcTemplate.queryForList("SELECT * FROM users WHERE username = '" + username + "'"));
        String latestSessionID = returnList.get(0).get("recent_session_id").toString();
        return latestSessionID.equals(sessionID);
        

        /**for(UserDataObject user : users) {
            if(user.getUsername().equals(username) && user.authenticateSessionID(sessionID)) {
                return true;
            }
        }
        return false;*/
    }

    public String generateSessionID(String username, JdbcTemplate jdbcTemplate) throws NoSuchAlgorithmException {
        if(checkUser(username,jdbcTemplate) == false) {
            System.out.println("User Does Not Exist");
            return null;
        }
        System.out.println("Generating Session ID");
        String sessionID = this.generateAUserSessionID();
        System.out.println("Session ID Generated: " + sessionID);
        jdbcTemplate.execute("UPDATE users SET recent_session_id = '" + sessionID + "' WHERE username = '" + username + "'");
        return sessionID;
        

        /* 
        for(UserDataObject user : users) {
            if(user.getUsername().equals(username)) {
                return user.generateSessionID();
            }
        }
        return null;
        */
    }

    public boolean logout(String username, String sessionID, JdbcTemplate jdbcTemplate) throws NoSuchAlgorithmException {
        if(this.authenticateUserPassword(username, sessionID, jdbcTemplate)) {
            System.out.println("User Not Authenticated and Could Not Be Logout");
            return false;
        }
        jdbcTemplate.execute("UPDATE users SET recent_session_id = NULL WHERE username = '" + username + "'");
        return true;
        


        /*for(UserDataObject user : users) {
            if(user.getUsername().equals(username) && user.authenticateSessionID(sessionID)) {
                user.logout();
                return true;
            }
        }
        return false;*/
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