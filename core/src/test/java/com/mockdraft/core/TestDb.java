package com.mockdraft.core;

import java.util.UUID;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

/** Fresh in-memory H2 database in MariaDB mode, matching how the console app runs. */
public final class TestDb {

    private TestDb() {}

    public static DataSource create() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=MariaDB;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;IGNORECASE=TRUE;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        return ds;
    }

    public static MockDraftCore core() {
        // low BCrypt cost keeps tests fast
        return new MockDraftCore(create(), java.time.Clock.systemUTC(), 4, MockDraftCore.DEFAULT_SESSION_TTL,
                MockDraftCore.DEFAULT_RESET_TTL);
    }
}
