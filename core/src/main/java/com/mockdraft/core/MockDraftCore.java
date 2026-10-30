package com.mockdraft.core;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;

import javax.sql.DataSource;

import com.mockdraft.core.auth.PasswordHasher;
import com.mockdraft.core.db.Database;
import com.mockdraft.core.db.Migrator;
import com.mockdraft.core.repo.PlayerRepository;
import com.mockdraft.core.service.AuthService;
import com.mockdraft.core.service.DraftService;

/**
 * One-stop wiring for the core: give it a {@link DataSource} and it migrates the schema and exposes the
 * services. The web app and the console app both start here, so they share every rule and every query.
 */
public final class MockDraftCore {

    public static final Duration DEFAULT_SESSION_TTL = Duration.ofDays(7);
    public static final Duration DEFAULT_RESET_TTL = Duration.ofMinutes(30);
    public static final int DEFAULT_BCRYPT_STRENGTH = 12;

    private final Database database;
    private final AuthService auth;
    private final DraftService drafts;

    public MockDraftCore(DataSource dataSource) {
        this(dataSource, Clock.systemUTC(), DEFAULT_BCRYPT_STRENGTH, DEFAULT_SESSION_TTL, DEFAULT_RESET_TTL);
    }

    public MockDraftCore(DataSource dataSource, Clock clock, int bcryptStrength, Duration sessionTtl, Duration resetTtl) {
        this.database = new Database(dataSource);
        new Migrator(database).migrate();
        PlayerRepository players = new PlayerRepository(database);
        this.auth = new AuthService(database, new PasswordHasher(bcryptStrength), clock, sessionTtl, resetTtl);
        this.drafts = new DraftService(database, players, new SecureRandom(), clock);
    }

    public Database database() { return database; }
    public AuthService auth() { return auth; }
    public DraftService drafts() { return drafts; }
}
