package com.mockdraft.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.mockdraft.core.MockDraftCore;

/** Entry point: {@code java -jar mockdraft-console.jar [--help]}. */
public final class ConsoleMain {

    private ConsoleMain() {}

    public static void main(String[] args) {
        ConsoleConfig config;
        try {
            config = ConsoleConfig.from(args, System.getenv());
        } catch (ConsoleConfig.HelpRequested e) {
            System.out.print(ConsoleConfig.usage());
            return;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.print(ConsoleConfig.usage());
            System.exit(2);
            return;
        }
        System.setProperty("mockdraft.home", config.home().toString());

        Terminal terminal = new Terminal(new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)),
                System.out, System.console(), config.color() && System.console() != null);
        MockDraftCore core;
        try {
            if (config.isEmbedded()) {
                Files.createDirectories(config.home());
            }
            core = new MockDraftCore(new SimpleDataSource(config.dbUrl(), config.dbUser(), config.dbPassword()));
        } catch (Exception e) {
            terminal.error("Could not open the database: " + rootMessage(e));
            terminal.info("Check --db-url / MOCKDRAFT_DB_URL, or run with no options to use the built-in database.");
            System.exit(1);
            return;
        }
        try {
            new ConsoleApp(terminal, core).run();
        } catch (Terminal.EndOfInput e) {
            terminal.println();
        }
    }

    private static String rootMessage(Throwable e) {
        Throwable root = e;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage();
    }
}
