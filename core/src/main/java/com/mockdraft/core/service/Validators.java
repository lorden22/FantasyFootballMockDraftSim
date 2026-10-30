package com.mockdraft.core.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/** Input rules shared by the web API and the console so both enforce exactly the same constraints. */
public final class Validators {

    public static final int MIN_TEAMS = 4;
    public static final int MAX_TEAMS = 20;
    public static final int PASSWORD_MIN = 8;
    public static final int PASSWORD_MAX = 72; // BCrypt ignores bytes beyond 72
    public static final int TEAM_NAME_MAX = 40;

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final Pattern TEAM_NAME = Pattern.compile("^[\\p{L}\\p{N} _.'&!?#-]+$");
    private static final Set<String> COMMON_PASSWORDS = Set.of(
            "password", "password1", "password123", "12345678", "123456789", "1234567890", "qwerty123",
            "qwertyuiop", "11111111", "00000000", "iloveyou", "football", "football1", "letmein1",
            "welcome1", "abc12345", "admin123", "baseball", "sunshine", "princess", "fantasy1",
            "mockdraft", "trustno1", "passw0rd");

    private Validators() {}

    /** Names that could be confused with system principals or staff. */
    private static final Set<String> RESERVED_USERNAMES = Set.of("anonymous", "admin", "administrator", "root", "system",
            "support", "mockdraft", "null", "undefined");

    public static String username(String value) {
        if (value == null || !USERNAME.matcher(value).matches()) {
            throw new ValidationException("username", "Username must be 3-30 characters: letters, numbers or underscores.");
        }
        if (RESERVED_USERNAMES.contains(value.toLowerCase(Locale.ROOT))) {
            throw new ValidationException("username", "That username is reserved. Please choose another.");
        }
        return value;
    }

    public static void newPassword(String password, String username) {
        if (password == null || password.length() < PASSWORD_MIN) {
            throw new ValidationException("password", "Password must be at least " + PASSWORD_MIN + " characters.");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > PASSWORD_MAX) {
            throw new ValidationException("password", "Password must be at most " + PASSWORD_MAX + " characters.");
        }
        if (password.chars().anyMatch(Character::isISOControl)) {
            throw new ValidationException("password", "Password contains invalid characters.");
        }
        if (password.isBlank()) {
            throw new ValidationException("password", "Password cannot be only spaces.");
        }
        String lower = password.toLowerCase(Locale.ROOT);
        if (username != null && lower.contains(username.toLowerCase(Locale.ROOT))) {
            throw new ValidationException("password", "Password must not contain your username.");
        }
        if (COMMON_PASSWORDS.contains(lower)) {
            throw new ValidationException("password", "That password is too common. Choose something harder to guess.");
        }
    }

    /** Returns the normalized email, or null if blank. */
    public static String optionalEmail(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return email(value);
    }

    public static String email(String value) {
        String email = value == null ? "" : value.strip().toLowerCase(Locale.ROOT);
        if (email.length() > 254 || !EMAIL.matcher(email).matches()) {
            throw new ValidationException("email", "Enter a valid email address.");
        }
        return email;
    }

    public static String teamName(String value) {
        String name = value == null ? "" : value.strip().replaceAll("\\s+", " ");
        if (name.isEmpty() || name.length() > TEAM_NAME_MAX) {
            throw new ValidationException("teamName", "Team name must be 1-" + TEAM_NAME_MAX + " characters.");
        }
        if (!TEAM_NAME.matcher(name).matches()) {
            throw new ValidationException("teamName", "Team name may use letters, numbers, spaces and _ . ' & ! ? # -");
        }
        return name;
    }

    public static int numTeams(int numTeams) {
        if (numTeams < MIN_TEAMS || numTeams > MAX_TEAMS) {
            throw new ValidationException("numTeams", "League size must be between " + MIN_TEAMS + " and " + MAX_TEAMS + " teams.");
        }
        return numTeams;
    }

    public static void draftPosition(Integer position, int numTeams) {
        if (position != null && (position < 1 || position > numTeams)) {
            throw new ValidationException("draftPosition", "Draft position must be between 1 and " + numTeams + ".");
        }
    }
}
