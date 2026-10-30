package com.mockdraft.core.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

import com.mockdraft.core.db.Database;

public final class UserRepository {

    private static final String COLUMNS = "username, email, password_hash, legacy_salt, created_at";

    private final Database db;

    public UserRepository(Database db) {
        this.db = db;
    }

    public Optional<UserAccount> find(String username) {
        return db.queryOne("SELECT " + COLUMNS + " FROM users WHERE username = ?", UserRepository::map, username);
    }

    public Optional<UserAccount> findByEmail(String email) {
        return db.queryOne("SELECT " + COLUMNS + " FROM users WHERE email = ?", UserRepository::map, email);
    }

    /** Locks the user row for the rest of the current transaction. */
    public void lock(String username) {
        db.query("SELECT username FROM users WHERE username = ? FOR UPDATE", rs -> rs.getString(1), username);
    }

    public void insert(String username, String email, String passwordHash, Instant createdAt) {
        db.update("INSERT INTO users (" + COLUMNS + ") VALUES (?, ?, ?, NULL, ?)", username, email, passwordHash, createdAt);
    }

    public void updatePassword(String username, String passwordHash) {
        db.update("UPDATE users SET password_hash = ?, legacy_salt = NULL WHERE username = ?", passwordHash, username);
    }

    public void updateEmail(String username, String email) {
        db.update("UPDATE users SET email = ? WHERE username = ?", email, username);
    }

    public void delete(String username) {
        db.update("DELETE FROM users WHERE username = ?", username);
    }

    private static UserAccount map(ResultSet rs) throws SQLException {
        return new UserAccount(rs.getString("username"), rs.getString("email"), rs.getString("password_hash"),
                rs.getString("legacy_salt"), Database.instant(rs, "created_at"));
    }
}
