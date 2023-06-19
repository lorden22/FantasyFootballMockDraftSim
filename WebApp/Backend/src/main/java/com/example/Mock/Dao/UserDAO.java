package com.example.Mock.DAO;

public interface UserDAO {
    public boolean checkUser(String username, String password);
    public boolean checkUsername(String username);
}

