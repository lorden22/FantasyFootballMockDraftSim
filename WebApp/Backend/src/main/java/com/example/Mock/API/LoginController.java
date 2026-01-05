package com.example.Mock.API;

import com.example.Mock.Service.LoginServices;
import com.example.common.Logger;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/login")
@CrossOrigin(origins = {"https://localhost:5500", "https://127.0.0.1:5500", "https://localhost", "https://127.0.0.1", "https://fantasy-football-draft.fly.dev"})
public class LoginController {

    private final LoginServices loginServices;

    // Username: alphanumeric and underscore only, 3-50 characters
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    // Password: 8-100 characters, no control characters
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 100;

    @Autowired
    public LoginController(LoginServices loginServices) {
        this.loginServices = loginServices;
        Logger.logInfo("Login Services Initialized");
    }

    private void validateUsername(String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Username must be 3-50 characters, alphanumeric and underscores only");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Password must be 8-100 characters");
        }
        // Check for control characters
        for (char c : password.toCharArray()) {
            if (Character.isISOControl(c)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password contains invalid characters");
            }
        }
    }

    private void validateSessionID(String sessionID) {
        if (sessionID == null || sessionID.length() != 32 || !sessionID.matches("^[a-f0-9]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid session ID format");
        }
    }

    @GetMapping(path = "/checkUser/")
    public boolean checkUser(@RequestParam("username") String username){
        validateUsername(username);
        return loginServices.checkUser(username);
    }

    @PutMapping(path = "/addUser/")
    public boolean addUser(@RequestParam("username") String username, @RequestParam("password") String password) throws NoSuchAlgorithmException {
        validateUsername(username);
        validatePassword(password);
        boolean log = loginServices.addUser(username, password);
        Logger.logAuth(username, "ADD_USER_API", log ? "SUCCESS" : "FAILED");
        return log;
    }

    @DeleteMapping(path = "/removeUser/")
    public boolean removeUser(@RequestParam("username") String username) {
        validateUsername(username);
        return loginServices.removeUser(username);
    }

    @GetMapping(path = "/attemptLogin/")
    public boolean attemptLogin(@RequestParam("username") String username, @RequestParam("password") String password) throws NoSuchAlgorithmException {
        validateUsername(username);
        validatePassword(password);
        boolean log = loginServices.authenticateUserPassword(username, password);
        Logger.logAuth(username, "LOGIN_API", log ? "SUCCESS" : "FAILED");
        return log;
    }

    @GetMapping(path = "/attemptSession/")
    public boolean attemptedSessionID(@RequestParam("username") String username, @RequestParam("sessionID") String sessionID) throws NoSuchAlgorithmException {
        validateUsername(username);
        validateSessionID(sessionID);
        boolean log = loginServices.authenticateUserSessionID(username, sessionID);
        Logger.logAuth(username, "SESSION_API", log ? "SUCCESS" : "FAILED");
        return log;
    }

    @GetMapping(path = "/generateSessionID/")
    public String generateSessionID(@RequestParam("username") String username) throws NoSuchAlgorithmException {
        validateUsername(username);
        return loginServices.generateSessionID(username);
    }

    @PutMapping(path = "/logout/")
    public boolean logout(@RequestParam("username") String username, @RequestParam("sessionID") String sessionID) throws NoSuchAlgorithmException {
        validateUsername(username);
        validateSessionID(sessionID);
        return loginServices.logout(username, sessionID);
    }
}
