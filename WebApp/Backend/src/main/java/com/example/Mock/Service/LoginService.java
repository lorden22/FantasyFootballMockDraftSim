package com.example.Mock.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.stereotype.Service;

import com.example.Mock.DAO.UserDataObject;

@Service
public class LoginService {

    private HashSet<UserDataObject> users;

    public LoginService() {
        this.users = new HashSet<UserDataObject>();
    }

    public boolean addUser(String usernameWanted, String passwordWanted) throws NoSuchAlgorithmException {
        for(UserDataObject user : users) {
            if(user.getUsername().equals(usernameWanted)) {
                return false;
            }
        }
        users.add(new UserDataObject(usernameWanted, passwordWanted));
        return true;
    }

    public void removeUser(UserDataObject usernameToDelete) {
        if(users.contains(usernameToDelete)) {
            users.remove(usernameToDelete);
        }
    }

    private boolean authenticateUser(String username, String password) throws NoSuchAlgorithmException {
        for(UserDataObject user : users) {
            if(user.getUsername().equals(username) && user.authenticateUser(password)) {
                return true;
            }
            return false;
        }
        return false;
    }

}