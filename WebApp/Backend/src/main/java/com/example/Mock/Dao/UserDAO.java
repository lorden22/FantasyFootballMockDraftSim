package com.example.Mock.DAO;

public interface UserDAO {
    public boolean authenticateUser(String attemptePassword) throws Exception;
    public String getUsername();
}

