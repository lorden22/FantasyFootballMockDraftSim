package com.mockdraft.core.repo;

import java.time.Instant;
import java.util.Optional;

import com.mockdraft.core.db.Database;

/**
 * Stores hashed, expiring tokens for one table ({@code sessions} or {@code password_resets}).
 * Only SHA-256 hashes are persisted, so a database leak does not expose usable tokens.
 */
public final class TokenRepository {

    private final Database db;
    private final String table;
    private final boolean hasCreatedAt;

    private TokenRepository(Database db, String table, boolean hasCreatedAt) {
        this.db = db;
        this.table = table;
        this.hasCreatedAt = hasCreatedAt;
    }

    public static TokenRepository sessions(Database db) {
        return new TokenRepository(db, "sessions", true);
    }

    public static TokenRepository passwordResets(Database db) {
        return new TokenRepository(db, "password_resets", false);
    }

    public void insert(String tokenHash, String username, Instant now, Instant expiresAt) {
        if (hasCreatedAt) {
            db.update("INSERT INTO " + table + " (token_hash, username, created_at, expires_at) VALUES (?, ?, ?, ?)",
                    tokenHash, username, now, expiresAt);
        } else {
            db.update("INSERT INTO " + table + " (token_hash, username, expires_at) VALUES (?, ?, ?)",
                    tokenHash, username, expiresAt);
        }
    }

    /** Username owning an unexpired token. */
    public Optional<String> findUsername(String tokenHash, Instant now) {
        return db.queryOne("SELECT username FROM " + table + " WHERE token_hash = ? AND expires_at > ?",
                rs -> rs.getString(1), tokenHash, now);
    }

    public void delete(String tokenHash) {
        db.update("DELETE FROM " + table + " WHERE token_hash = ?", tokenHash);
    }

    public void deleteAllFor(String username) {
        db.update("DELETE FROM " + table + " WHERE username = ?", username);
    }

    public void deleteAllForExcept(String username, String keepTokenHash) {
        db.update("DELETE FROM " + table + " WHERE username = ? AND token_hash <> ?", username, keepTokenHash);
    }

    public int deleteExpired(Instant now) {
        return db.update("DELETE FROM " + table + " WHERE expires_at <= ?", now);
    }
}
