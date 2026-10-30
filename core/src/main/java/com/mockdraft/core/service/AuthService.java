package com.mockdraft.core.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mockdraft.core.auth.PasswordHasher;
import com.mockdraft.core.auth.Tokens;
import com.mockdraft.core.db.Database;
import com.mockdraft.core.db.DuplicateKeyException;
import com.mockdraft.core.model.User;
import com.mockdraft.core.repo.TokenRepository;
import com.mockdraft.core.repo.UserAccount;
import com.mockdraft.core.repo.UserRepository;

/** Accounts, sign-in, sessions and password reset. Shared by the web app and the console app. */
public final class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String BAD_CREDENTIALS = "Invalid username or password.";

    public record Session(String token, Instant expiresAt) {}

    /** Returned when a reset was requested for an existing account, so the caller can email the link. */
    public record PasswordResetRequest(String username, String email, String token, Instant expiresAt) {}

    private final Database db;
    private final UserRepository users;
    private final TokenRepository sessions;
    private final TokenRepository resets;
    private final PasswordHasher hasher;
    private final Clock clock;
    private final Duration sessionTtl;
    private final Duration resetTtl;

    public AuthService(Database db, PasswordHasher hasher, Clock clock, Duration sessionTtl, Duration resetTtl) {
        this.db = db;
        this.users = new UserRepository(db);
        this.sessions = TokenRepository.sessions(db);
        this.resets = TokenRepository.passwordResets(db);
        this.hasher = hasher;
        this.clock = clock;
        this.sessionTtl = sessionTtl;
        this.resetTtl = resetTtl;
    }

    public User register(String username, String password, String email) {
        Validators.username(username);
        Validators.newPassword(password, username);
        String normalizedEmail = Validators.optionalEmail(email);
        String hash = hasher.hash(password);
        return db.inTransaction(() -> {
            if (users.find(username).isPresent()) {
                throw new ConflictException("That username is taken.");
            }
            if (normalizedEmail != null && users.findByEmail(normalizedEmail).isPresent()) {
                throw new ConflictException("An account already uses that email.");
            }
            Instant now = clock.instant();
            try {
                users.insert(username, normalizedEmail, hash, now);
            } catch (DuplicateKeyException race) {
                throw new ConflictException("That username or email is already in use.");
            }
            log.info("Registered user {}", username);
            return new User(username, normalizedEmail, now);
        });
    }

    /** Verifies credentials. Upgrades legacy SHA-256 hashes to BCrypt on success. */
    public User authenticate(String username, String password) {
        if (username == null || username.isBlank() || username.length() > 50 || password == null || password.length() > 200) {
            hasher.burnTime(password);
            throw new AuthenticationException(BAD_CREDENTIALS);
        }
        Optional<UserAccount> found = users.find(username);
        if (found.isEmpty()) {
            hasher.burnTime(password);
            throw new AuthenticationException(BAD_CREDENTIALS);
        }
        UserAccount account = found.get();
        if (!hasher.matches(password, account.passwordHash(), account.legacySalt())) {
            throw new AuthenticationException(BAD_CREDENTIALS);
        }
        if (account.legacySalt() != null) {
            users.updatePassword(account.username(), hasher.hash(password));
            log.info("Upgraded legacy password hash for {}", account.username());
        }
        return account.toUser();
    }

    public Session createSession(String username) {
        String token = Tokens.newToken();
        Instant now = clock.instant();
        Instant expires = now.plus(sessionTtl);
        sessions.insert(Tokens.hash(token), username, now, expires);
        return new Session(token, expires);
    }

    /** Username for a valid, unexpired session token. */
    public Optional<String> resolveSession(String token) {
        if (!Tokens.looksLikeToken(token)) {
            return Optional.empty();
        }
        return sessions.findUsername(Tokens.hash(token), clock.instant());
    }

    public Optional<User> currentUser(String username) {
        return users.find(username).map(UserAccount::toUser);
    }

    public void logout(String token) {
        if (Tokens.looksLikeToken(token)) {
            sessions.delete(Tokens.hash(token));
        }
    }

    /**
     * Starts a reset for the account with this email. Returns empty when no such account exists; callers
     * must respond identically in both cases so the endpoint cannot be used to discover emails.
     */
    public Optional<PasswordResetRequest> requestPasswordReset(String email) {
        String normalized = Validators.email(email);
        Optional<UserAccount> account = users.findByEmail(normalized);
        if (account.isEmpty()) {
            return Optional.empty();
        }
        String username = account.get().username();
        String token = Tokens.newToken();
        Instant expires = clock.instant().plus(resetTtl);
        db.inTransaction(() -> {
            resets.deleteAllFor(username); // only the newest link works
            resets.insert(Tokens.hash(token), username, clock.instant(), expires);
        });
        return Optional.of(new PasswordResetRequest(username, normalized, token, expires));
    }

    public boolean isResetTokenValid(String token) {
        return Tokens.looksLikeToken(token) && resets.findUsername(Tokens.hash(token), clock.instant()).isPresent();
    }

    /** Sets a new password from a reset link, then revokes the link and every session for the account. */
    public void resetPassword(String token, String newPassword) {
        if (!Tokens.looksLikeToken(token)) {
            throw new ValidationException("token", "This reset link is invalid or has expired.");
        }
        String tokenHash = Tokens.hash(token);
        db.inTransaction(() -> {
            String username = resets.findUsername(tokenHash, clock.instant())
                    .orElseThrow(() -> new ValidationException("token", "This reset link is invalid or has expired."));
            Validators.newPassword(newPassword, username);
            users.updatePassword(username, hasher.hash(newPassword));
            resets.deleteAllFor(username);
            sessions.deleteAllFor(username);
            log.info("Password reset completed for {}", username);
        });
    }

    public User changeEmail(String username, String currentPassword, String email) {
        requirePassword(username, currentPassword, "currentPassword");
        String normalized = Validators.optionalEmail(email);
        return db.inTransaction(() -> {
            if (normalized != null) {
                Optional<UserAccount> other = users.findByEmail(normalized);
                if (other.isPresent() && !other.get().username().equals(username)) {
                    throw new ConflictException("An account already uses that email.");
                }
            }
            users.updateEmail(username, normalized);
            return users.find(username).orElseThrow().toUser();
        });
    }

    /** Changes the password and signs out every other session (pass null to sign out all). */
    public void changePassword(String username, String currentPassword, String newPassword, String keepSessionToken) {
        requirePassword(username, currentPassword, "currentPassword");
        Validators.newPassword(newPassword, username);
        String hash = hasher.hash(newPassword);
        db.inTransaction(() -> {
            users.updatePassword(username, hash);
            if (Tokens.looksLikeToken(keepSessionToken)) {
                sessions.deleteAllForExcept(username, Tokens.hash(keepSessionToken));
            } else {
                sessions.deleteAllFor(username);
            }
            resets.deleteAllFor(username);
        });
    }

    /** Permanently deletes the account; drafts, sessions and reset tokens cascade. */
    public void deleteAccount(String username, String password) {
        requirePassword(username, password, "password");
        users.delete(username);
        log.info("Deleted account {}", username);
    }

    public void purgeExpired() {
        Instant now = clock.instant();
        int removed = sessions.deleteExpired(now) + resets.deleteExpired(now);
        if (removed > 0) {
            log.debug("Purged {} expired tokens", removed);
        }
    }

    private void requirePassword(String username, String password, String field) {
        UserAccount account = users.find(username).orElseThrow(() -> new AuthenticationException("Not signed in."));
        if (!hasher.matches(password, account.passwordHash(), account.legacySalt())) {
            throw new ValidationException(field, "Current password is incorrect.");
        }
    }
}
