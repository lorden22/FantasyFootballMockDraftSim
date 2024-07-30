package com.example.Mock.API;

import com.example.Mock.Service.LoginServices;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/login")
@CrossOrigin
public class LoginController {

    private final LoginServices loginServices;

    @Autowired
    public LoginController(LoginServices loginServices) {
        this.loginServices = loginServices;
        System.out.println("Login Services Initialized");
    }

    @GetMapping(path = "/checkUser/")
    public boolean checkUser(@RequestParam("username") String username){
        return loginServices.checkUser(username);
    }

    @PutMapping(path = "/addUser/")
    public boolean addUser(@RequestParam("username") String username, @RequestParam("password") String password) throws NoSuchAlgorithmException {
        boolean log = loginServices.addUser(username, password);
        System.out.println(log + " " + username + " " + password);
        return log;
    }

    @DeleteMapping(path = "/removeUser/")
    public boolean removeUser(@RequestParam("username") String username) {
        return loginServices.removeUser(username);
    }

    @GetMapping(path = "/attemptLogin/")
    public boolean attemptLogin(@RequestParam("username") String username, @RequestParam("password") String password) throws NoSuchAlgorithmException {
        boolean log = loginServices.authenticateUserPassword(username, password);
        System.out.println(log + " " + username + " " + password);
        return log;
    }

    @GetMapping(path = "/attemptSession/")
    public boolean attemptedSessionID(@RequestParam("username") String username, @RequestParam("sessionID") String sessionID) throws NoSuchAlgorithmException {
        boolean log = loginServices.authenticateUserSessionID(username, sessionID);
        System.out.println(log + " " + username + " " + sessionID);
        return log;
    }

    @GetMapping(path = "/generateSessionID/") 
    public String generateSessionID(@RequestParam("username") String username) throws NoSuchAlgorithmException {
        return loginServices.generateSessionID(username);
    }

    @PostMapping(path = "/logout/")
    public boolean logout(@RequestParam("username") String username, @RequestParam("sessionID") String sessionID) throws NoSuchAlgorithmException {
        return loginServices.logout(username, sessionID);
    }
}
