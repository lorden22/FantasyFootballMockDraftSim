package com.example.Mock.API;

import com.example.Mock.Service.LoginServices;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/login")
@CrossOrigin
@RestController
public class LoginController {

    private final LoginServices loginServices;

    @Autowired
    public LoginController(LoginServices loginServices) {
        this.loginServices = loginServices;
    }

    @GetMapping(path = "/checkUser/")
    public boolean checkUser(@RequestParam("username") String username) {
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
        boolean log = loginServices.authenticateUser(username, password);
        System.out.println(log + " " + username + " " + password);
        return log;
    }
}

