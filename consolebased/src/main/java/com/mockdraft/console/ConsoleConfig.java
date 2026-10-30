package com.mockdraft.console;

import java.nio.file.Path;
import java.util.Map;

/**
 * Database and display settings. Precedence: command-line flag, then environment variable, then default.
 *
 * <pre>
 *   --db-url URL        MOCKDRAFT_DB_URL       default: embedded H2 file in ~/.mockdraft
 *   --db-user USER      MOCKDRAFT_DB_USER
 *   --db-password PW    MOCKDRAFT_DB_PASSWORD  (prefer the env var: flags are visible in the process list)
 *   --no-color          NO_COLOR
 * </pre>
 */
public record ConsoleConfig(String dbUrl, String dbUser, String dbPassword, boolean color, Path home) {

    public static ConsoleConfig from(String[] args, Map<String, String> env) {
        Path home = Path.of(env.getOrDefault("MOCKDRAFT_HOME", System.getProperty("user.home") + "/.mockdraft"));
        String url = env.get("MOCKDRAFT_DB_URL");
        String user = env.get("MOCKDRAFT_DB_USER");
        String password = env.get("MOCKDRAFT_DB_PASSWORD");
        boolean color = !env.containsKey("NO_COLOR");

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--db-url" -> url = value(args, ++i);
                case "--db-user" -> user = value(args, ++i);
                case "--db-password" -> password = value(args, ++i);
                case "--no-color" -> color = false;
                case "-h", "--help" -> throw new HelpRequested();
                default -> throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
        }
        if (url == null || url.isBlank()) {
            url = embeddedUrl(home);
            user = "sa";
            password = "";
        }
        return new ConsoleConfig(url, user, password == null ? "" : password, color, home);
    }

    public static String embeddedUrl(Path home) {
        return "jdbc:h2:file:" + home.resolve("mockdraft").toAbsolutePath()
                + ";MODE=MariaDB;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;IGNORECASE=TRUE;DB_CLOSE_DELAY=-1";
    }

    public boolean isEmbedded() {
        return dbUrl.startsWith("jdbc:h2:");
    }

    private static String value(String[] args, int i) {
        if (i >= args.length) {
            throw new IllegalArgumentException("Missing value for " + args[i - 1]);
        }
        return args[i];
    }

    public static final class HelpRequested extends RuntimeException {
        HelpRequested() {
            super(null, null, false, false);
        }
    }

    public static String usage() {
        return """
                Usage: java -jar mockdraft-console.jar [options]

                With no options the app keeps its data in an embedded database under ~/.mockdraft
                (override with MOCKDRAFT_HOME). To share accounts and drafts with the web app, point it
                at the same MariaDB:

                  --db-url URL        JDBC URL, e.g. jdbc:mariadb://localhost:3306/mockdraft  (env MOCKDRAFT_DB_URL)
                  --db-user USER      database user                                          (env MOCKDRAFT_DB_USER)
                  --db-password PW    database password; prefer env MOCKDRAFT_DB_PASSWORD
                  --no-color          disable ANSI colors                                    (env NO_COLOR)
                  -h, --help          show this help
                """;
    }
}
