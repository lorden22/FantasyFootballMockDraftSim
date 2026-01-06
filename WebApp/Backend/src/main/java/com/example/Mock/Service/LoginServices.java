package com.example.Mock.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Mock.DAO.UserDataObject;
import com.example.common.Logger;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Service
public class LoginServices {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    // Session expiration time (24 hours)
    private static final long SESSION_EXPIRATION_HOURS = 24;

    // Rate limiting: max 5 login attempts per minute per IP/username
    private final ConcurrentHashMap<String, Bucket> rateLimitBuckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    public Bucket resolveBucket(String key) {
        return rateLimitBuckets.computeIfAbsent(key, k -> createNewBucket());
    }

    public boolean isRateLimited(String key) {
        Bucket bucket = resolveBucket(key);
        return !bucket.tryConsume(1);
    }

    public boolean checkUser(String username) {
        Logger.logAuth(username, "CHECK_USER", "ATTEMPT");
        String sql = "SELECT * FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        ArrayList<Map<String, Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        boolean userExists = returnList.size() != 0;
        Logger.logAuth(username, "CHECK_USER", userExists ? "USER_FOUND" : "USER_NOT_FOUND");
        return userExists;
    }

    public boolean addUser(String usernameWanted, String passwordWanted) throws NoSuchAlgorithmException {
        Logger.logAuth(usernameWanted, "ADD_USER", "ATTEMPT");
        if (checkUser(usernameWanted)) {
            Logger.logAuth(usernameWanted, "ADD_USER", "FAILED_USER_EXISTS");
            return false;
        }
        UserDataObject userToAdd = new UserDataObject(usernameWanted, passwordWanted, this.namedParameterJdbcTemplate);
        boolean success = userToAdd.addUserToDatabase();
        Logger.logAuth(usernameWanted, "ADD_USER", success ? "SUCCESS" : "FAILED");
        return success;
    }

    public boolean removeUser(String username) {
        Logger.logAuth(username, "REMOVE_USER", "ATTEMPT");
        if (!checkUser(username)) {
            Logger.logAuth(username, "REMOVE_USER", "FAILED_USER_NOT_EXISTS");
            return false;
        }
        String sql = "DELETE FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        namedParameterJdbcTemplate.update(sql, params);
        Logger.logAuth(username, "REMOVE_USER", "SUCCESS");
        return true;
    }

    public boolean authenticateUserPassword(String username, String password) throws NoSuchAlgorithmException {
        Logger.logAuth(username, "AUTH_PASSWORD", "ATTEMPT");
        if (!checkUser(username)) {
            Logger.logAuth(username, "AUTH_PASSWORD", "FAILED_USER_NOT_EXISTS");
            return false;
        }

        String sql = "SELECT * FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        ArrayList<Map<String, Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        String salt = returnList.get(0).get("salt").toString();
        String hashPassword = returnList.get(0).get("hash_pass").toString();

        boolean authenticated;
        if ("BCRYPT".equals(salt)) {
            // New BCrypt password
            authenticated = passwordEncoder.matches(password, hashPassword);
        } else {
            // Legacy SHA-256 password - verify and migrate to BCrypt
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(salt.getBytes());
            byte[] hash = digest.digest(password.getBytes());
            String attemptedHashPassword = byteArrayToString(hash);
            authenticated = attemptedHashPassword.equals(hashPassword);

            // If authenticated with legacy hash, upgrade to BCrypt
            if (authenticated) {
                migratePasswordToBCrypt(username, password);
            }
        }

        Logger.logAuth(username, "AUTH_PASSWORD", authenticated ? "SUCCESS" : "FAILED_WRONG_PASSWORD");
        return authenticated;
    }

    private void migratePasswordToBCrypt(String username, String password) {
        String newHash = passwordEncoder.encode(password);
        String sql = "UPDATE users SET hash_pass = :hash_pass, salt = :salt WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("hash_pass", newHash);
        params.addValue("salt", "BCRYPT");
        params.addValue("username", username);
        namedParameterJdbcTemplate.update(sql, params);
        Logger.logAuth(username, "PASSWORD_MIGRATED", "SUCCESS");
    }

    public boolean authenticateUserSessionID(String username, String sessionID) throws NoSuchAlgorithmException {
        Logger.logAuth(username, "AUTH_SESSION", "ATTEMPT");
        if (!checkUser(username)) {
            Logger.logAuth(username, "AUTH_SESSION", "FAILED_USER_NOT_EXISTS");
            return false;
        }

        String sql = "SELECT recent_session_id, session_created_at FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        ArrayList<Map<String, Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        Object sessionIdObj = returnList.get(0).get("recent_session_id");
        if (sessionIdObj == null) {
            Logger.logAuth(username, "AUTH_SESSION", "FAILED_NO_SESSION");
            return false;
        }

        String latestSessionID = sessionIdObj.toString();
        boolean sessionMatches = latestSessionID.equals(sessionID);

        if (!sessionMatches) {
            Logger.logAuth(username, "AUTH_SESSION", "FAILED_INVALID_SESSION");
            return false;
        }

        // Check session expiration
        Object sessionCreatedAtObj = returnList.get(0).get("session_created_at");
        if (sessionCreatedAtObj != null) {
            Timestamp sessionCreatedAt = (Timestamp) sessionCreatedAtObj;
            Instant sessionTime = sessionCreatedAt.toInstant();
            Instant now = Instant.now();
            long hoursSinceCreation = Duration.between(sessionTime, now).toHours();

            if (hoursSinceCreation > SESSION_EXPIRATION_HOURS) {
                Logger.logAuth(username, "AUTH_SESSION", "FAILED_SESSION_EXPIRED");
                // Clear expired session
                clearSession(username);
                return false;
            }
        }

        Logger.logAuth(username, "AUTH_SESSION", "SUCCESS");
        return true;
    }

    private void clearSession(String username) {
        String sql = "UPDATE users SET recent_session_id = NULL, session_created_at = NULL WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public String generateSessionID(String username) throws NoSuchAlgorithmException {
        Logger.logAuth(username, "GENERATE_SESSION", "ATTEMPT");
        if (!checkUser(username)) {
            Logger.logAuth(username, "GENERATE_SESSION", "FAILED_USER_NOT_EXISTS");
            return null;
        }
        String sessionID = this.generateAUserSessionID();
        Logger.logAuth(username, "GENERATE_SESSION", "SUCCESS");

        String sql = "UPDATE users SET recent_session_id = :sessionID, session_created_at = NOW() WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sessionID", sessionID);
        params.addValue("username", username);

        namedParameterJdbcTemplate.update(sql, params);
        return sessionID;
    }

    public boolean logout(String username, String sessionID) throws NoSuchAlgorithmException {
        Logger.logAuth(username, "LOGOUT", "ATTEMPT");
        if (!this.authenticateUserSessionID(username, sessionID)) {
            Logger.logAuth(username, "LOGOUT", "FAILED_NOT_AUTHENTICATED");
            return false;
        }

        clearSession(username);
        Logger.logAuth(username, "LOGOUT", "SUCCESS");
        return true;
    }

    // Password reset functionality
    public String generatePasswordResetToken(String username) {
        Logger.logAuth(username, "GENERATE_RESET_TOKEN", "ATTEMPT");
        if (!checkUser(username)) {
            Logger.logAuth(username, "GENERATE_RESET_TOKEN", "FAILED_USER_NOT_EXISTS");
            return null;
        }

        String token = generateAUserSessionID() + generateAUserSessionID(); // 64 char token
        // Token expires in 1 hour
        String sql = "UPDATE users SET reset_token = :token, reset_token_expires = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("token", token);
        params.addValue("username", username);
        namedParameterJdbcTemplate.update(sql, params);

        Logger.logAuth(username, "GENERATE_RESET_TOKEN", "SUCCESS");
        return token;
    }

    public boolean validatePasswordResetToken(String token) {
        String sql = "SELECT username FROM users WHERE reset_token = :token AND reset_token_expires > NOW()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("token", token);

        ArrayList<Map<String, Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        return returnList.size() > 0;
    }

    public boolean resetPassword(String token, String newPassword) {
        Logger.logAuth("TOKEN", "RESET_PASSWORD", "ATTEMPT");

        String sql = "SELECT username FROM users WHERE reset_token = :token AND reset_token_expires > NOW()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("token", token);

        ArrayList<Map<String, Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        if (returnList.size() == 0) {
            Logger.logAuth("TOKEN", "RESET_PASSWORD", "FAILED_INVALID_TOKEN");
            return false;
        }

        String username = returnList.get(0).get("username").toString();
        String newHash = passwordEncoder.encode(newPassword);

        String updateSql = "UPDATE users SET hash_pass = :hash_pass, salt = :salt, reset_token = NULL, reset_token_expires = NULL WHERE username = :username";
        MapSqlParameterSource updateParams = new MapSqlParameterSource();
        updateParams.addValue("hash_pass", newHash);
        updateParams.addValue("salt", "BCRYPT");
        updateParams.addValue("username", username);
        namedParameterJdbcTemplate.update(updateSql, updateParams);

        Logger.logAuth(username, "RESET_PASSWORD", "SUCCESS");
        return true;
    }

    public String getUserEmail(String username) {
        String sql = "SELECT email FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);

        ArrayList<Map<String, Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        if (returnList.size() > 0 && returnList.get(0).get("email") != null) {
            return returnList.get(0).get("email").toString();
        }
        return null;
    }

    public boolean setUserEmail(String username, String email) {
        if (!checkUser(username)) {
            return false;
        }
        String sql = "UPDATE users SET email = :email WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        params.addValue("username", username);
        namedParameterJdbcTemplate.update(sql, params);
        return true;
    }

    public String getUsernameByEmail(String email) {
        String sql = "SELECT username FROM users WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);

        ArrayList<Map<String, Object>> returnList = new ArrayList<>(namedParameterJdbcTemplate.queryForList(sql, params));
        if (returnList.size() > 0) {
            return returnList.get(0).get("username").toString();
        }
        return null;
    }

    private static String byteArrayToString(byte[] bytes) {
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
        return byteArrayToString(bytes);
    }
}
