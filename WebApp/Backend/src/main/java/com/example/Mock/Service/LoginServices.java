package com.example.Mock.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.stereotype.Service;

import com.example.Mock.DAO.UserDataObject;

@Service
public class LoginServices{

    private HashSet<UserDataObject> users;

    public LoginServices() {
        this.users = new HashSet<UserDataObject>();
    }

    public boolean checkUser(String username) {
        for(UserDataObject user : users) {
            if(user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean addUser(String usernameWanted, String passwordWanted) throws NoSuchAlgorithmException {
        if(checkUser(usernameWanted) == true) {
            return false;
        }
        users.add(new UserDataObject(usernameWanted, passwordWanted));
        return true;
    }

    public boolean removeUser(String username) {
        for(UserDataObject user : users) {
            if(user.getUsername().equals(username)) {
                users.remove(user);
                return true;
            }
        }
        return false;
    }

    public boolean authenticateUser(String username, String password) throws NoSuchAlgorithmException {
        for(UserDataObject user : users) {
            if(user.getUsername().equals(username) && user.authenticateUser(password)) {
                return true;
            }
        }
        return false;
    }

}