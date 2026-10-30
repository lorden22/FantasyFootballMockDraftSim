package com.mockdraft.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

/**
 * Minimal JDBC helper shared by every repository. All SQL uses bound parameters.
 *
 * <p>Calls made inside {@link #inTransaction} share one connection bound to the current thread and
 * commit or roll back together; calls made outside run in their own auto-commit connection.
 */
public final class Database {

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    public interface TransactionWork<T> {
        T run();
    }

    private final DataSource dataSource;
    private final ThreadLocal<Connection> current = new ThreadLocal<>();

    public Database(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource dataSource() {
        return dataSource;
    }

    public <T> T inTransaction(TransactionWork<T> work) {
        if (current.get() != null) {
            return work.run();
        }
        try (Connection conn = dataSource.getConnection()) {
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            current.set(conn);
            try {
                T result = work.run();
                conn.commit();
                return result;
            } catch (RuntimeException | Error e) {
                conn.rollback();
                throw e;
            } finally {
                current.remove();
                conn.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw translate("Transaction failed", e);
        }
    }

    public void inTransaction(Runnable work) {
        inTransaction(() -> {
            work.run();
            return null;
        });
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        return withConnection(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    List<T> rows = new ArrayList<>();
                    while (rs.next()) {
                        rows.add(mapper.map(rs));
                    }
                    return rows;
                }
            }
        }, sql);
    }

    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> rows = query(sql, mapper, params);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public long queryLong(String sql, Object... params) {
        return queryOne(sql, rs -> rs.getLong(1), params).orElse(0L);
    }

    public int update(String sql, Object... params) {
        return withConnection(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                bind(ps, params);
                return ps.executeUpdate();
            }
        }, sql);
    }

    public long insertReturningKey(String sql, Object... params) {
        return withConnection(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bind(ps, params);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("No generated key returned");
                    }
                    return keys.getLong(1);
                }
            }
        }, sql);
    }

    public void batch(String sql, List<Object[]> rows) {
        if (rows.isEmpty()) {
            return;
        }
        withConnection(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Object[] row : rows) {
                    bind(ps, row);
                    ps.addBatch();
                }
                ps.executeBatch();
                return null;
            }
        }, sql);
    }

    /** Runs raw DDL (no parameters). Used by the migrator only. */
    public void execute(String sql) {
        withConnection(conn -> {
            try (Statement st = conn.createStatement()) {
                st.execute(sql);
                return null;
            }
        }, sql);
    }

    /** Gives direct access to the current (or a fresh) connection for metadata inspection. */
    public <T> T withConnection(SqlFunction<T> fn) {
        return withConnection(fn, "connection");
    }

    @FunctionalInterface
    public interface SqlFunction<T> {
        T apply(Connection conn) throws SQLException;
    }

    private <T> T withConnection(SqlFunction<T> fn, String context) {
        Connection bound = current.get();
        try {
            if (bound != null) {
                return fn.apply(bound);
            }
            try (Connection conn = dataSource.getConnection()) {
                return fn.apply(conn);
            }
        } catch (SQLException e) {
            throw translate("SQL failed: " + abbreviate(context), e);
        }
    }

    private static void bind(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object value = params[i];
            int index = i + 1;
            if (value == null) {
                ps.setNull(index, Types.NULL);
            } else if (value instanceof Instant instant) {
                ps.setTimestamp(index, Timestamp.from(instant));
            } else if (value instanceof Enum<?> e) {
                ps.setString(index, e.name());
            } else {
                ps.setObject(index, value);
            }
        }
    }

    public static Instant instant(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts == null ? null : ts.toInstant();
    }

    private static DataAccessException translate(String message, SQLException e) {
        String state = e.getSQLState();
        if (state != null && state.startsWith("23") && isUniqueViolation(e)) {
            return new DuplicateKeyException(message, e);
        }
        return new DataAccessException(message, e);
    }

    private static boolean isUniqueViolation(SQLException e) {
        // 23505 = standard unique violation (H2); MariaDB reports 23000 with error code 1062
        return "23505".equals(e.getSQLState()) || e.getErrorCode() == 1062;
    }

    private static String abbreviate(String sql) {
        String oneLine = sql.replaceAll("\\s+", " ").trim();
        return oneLine.length() > 80 ? oneLine.substring(0, 80) + "..." : oneLine;
    }
}
