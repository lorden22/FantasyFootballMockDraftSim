package com.example.Mock.DAO;

import com.example.Mock.DAO.UserDAO;

public class UserDataObject implements UserDAO {

    public boolean checkUser(String username, String password) {
        return true;
    }

    public boolean checkUsername(String username) {
        return true;
    }

}