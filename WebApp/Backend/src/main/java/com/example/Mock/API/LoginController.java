package com.example.Mock.API;

import com.example.Mock.Service.EmailService;
import com.example.Mock.Service.LoginServices;
import com.example.common.Logger;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/login")
@CrossOrigin(origins = {"https://localhost:5500", "https://127.0.0.1:5500", "https://localhost", "https://127.0.0.1", "https://fantasy-football-draft.fly.dev"})
public class LoginController {

    private final LoginServices loginServices;
    private final EmailService emailService;

    // Username: alphanumeric and underscore only, 3-50 characters
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    // Email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    // Password: 8-100 characters, no control characters
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 100;

    @Autowired
    public LoginController(LoginServices loginServices, EmailService emailService) {
        this.loginServices = loginServices;
        this.emailService = emailService;
        Logger.logInfo("Login Services Initialized");
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
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

    @PostMapping(path = "/attemptLogin/")
    public boolean attemptLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request) throws NoSuchAlgorithmException {
        validateUsername(username);
        validatePassword(password);

        // Rate limiting check
        String clientIP = getClientIP(request);
        String rateLimitKey = clientIP + ":" + username;
        if (loginServices.isRateLimited(rateLimitKey)) {
            Logger.logAuth(username, "LOGIN_API", "RATE_LIMITED");
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many login attempts. Please try again later.");
        }

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

    // Email management
    @PostMapping(path = "/setEmail/")
    public boolean setEmail(@RequestParam("username") String username, @RequestParam("email") String email) {
        validateUsername(username);
        validateEmail(email);
        return loginServices.setUserEmail(username, email);
    }

    @GetMapping(path = "/getEmail/")
    public String getEmail(@RequestParam("username") String username) {
        validateUsername(username);
        String email = loginServices.getUserEmail(username);
        return email != null ? email : "";
    }

    // Password reset endpoints
    @PostMapping(path = "/requestPasswordReset/")
    public boolean requestPasswordReset(@RequestParam("email") String email, HttpServletRequest request) {
        validateEmail(email);

        // Rate limit password reset requests
        String clientIP = getClientIP(request);
        if (loginServices.isRateLimited("reset:" + clientIP)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many password reset requests. Please try again later.");
        }

        String username = loginServices.getUsernameByEmail(email);
        if (username == null) {
            // Don't reveal whether email exists - return true anyway
            Logger.logAuth(email, "PASSWORD_RESET_REQUEST", "EMAIL_NOT_FOUND");
            return true;
        }

        String token = loginServices.generatePasswordResetToken(username);
        if (token != null) {
            try {
                emailService.sendPasswordResetEmail(email, username, token);
                Logger.logAuth(username, "PASSWORD_RESET_REQUEST", "EMAIL_SENT");
            } catch (Exception e) {
                Logger.logError("Failed to send password reset email: " + e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email");
            }
        }
        return true;
    }

    @GetMapping(path = "/validateResetToken/")
    public boolean validateResetToken(@RequestParam("token") String token) {
        if (token == null || token.length() != 64 || !token.matches("^[a-f0-9]+$")) {
            return false;
        }
        return loginServices.validatePasswordResetToken(token);
    }

    @PostMapping(path = "/resetPassword/")
    public boolean resetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        if (token == null || token.length() != 64 || !token.matches("^[a-f0-9]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token format");
        }
        validatePassword(password);
        return loginServices.resetPassword(token, password);
    }

    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
    }
}
