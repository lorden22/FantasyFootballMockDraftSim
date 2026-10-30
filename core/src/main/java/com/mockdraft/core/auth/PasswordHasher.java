package com.mockdraft.core.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt for all new hashes. Also verifies hashes from the pre-2.0 app (salted SHA-256) so those users
 * can still sign in; callers re-hash with BCrypt after a successful legacy match.
 */
public final class PasswordHasher {

    private final BCryptPasswordEncoder bcrypt;
    private final String dummyHash;

    public PasswordHasher(int strength) {
        this.bcrypt = new BCryptPasswordEncoder(strength);
        this.dummyHash = bcrypt.encode("timing-equalizer-not-a-real-password");
    }

    public String hash(String password) {
        return bcrypt.encode(password);
    }

    public boolean matches(String password, String storedHash, String legacySalt) {
        if (password == null || storedHash == null) {
            return false;
        }
        if (legacySalt != null) {
            return MessageDigest.isEqual(
                    Tokens.sha256Hex(legacySalt.getBytes(StandardCharsets.UTF_8), password.getBytes(StandardCharsets.UTF_8))
                            .getBytes(StandardCharsets.US_ASCII),
                    storedHash.getBytes(StandardCharsets.US_ASCII));
        }
        try {
            return bcrypt.matches(password, storedHash);
        } catch (IllegalArgumentException tooLong) {
            return false;
        }
    }

    /** Spends the same time as a real check so unknown usernames cannot be detected by timing. */
    public void burnTime(String password) {
        try {
            bcrypt.matches(password == null ? "" : password, dummyHash);
        } catch (IllegalArgumentException ignored) {
            // over-long input; nothing to equalize
        }
    }
}
