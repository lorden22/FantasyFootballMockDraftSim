package com.mockdraft.console;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;

/** Line-based terminal I/O with optional ANSI styling and masked password entry. */
public final class Terminal {

    /** Thrown when input ends (Ctrl-D / closed stdin) so the app can exit cleanly. */
    public static final class EndOfInput extends RuntimeException {
        EndOfInput() {
            super(null, null, false, false);
        }
    }

    private static final String RESET = "\u001B[0m";

    private final BufferedReader in;
    private final PrintStream out;
    private final Console console;
    private final boolean color;

    public Terminal(BufferedReader in, PrintStream out, Console console, boolean color) {
        this.in = in;
        this.out = out;
        this.console = console;
        this.color = color;
    }

    public void println() { out.println(); }
    public void println(String s) { out.println(s); }
    public void print(String s) { out.print(s); out.flush(); }
    public void printf(String fmt, Object... args) { out.printf(fmt, args); }

    public String prompt(String label) {
        print(accent("› ") + label);
        try {
            String line = in.readLine();
            if (line == null) {
                throw new EndOfInput();
            }
            return line.strip();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Reads a password without echo when attached to a real terminal. */
    public String password(String label) {
        if (console != null) {
            char[] chars = console.readPassword("%s", accent("› ") + label);
            if (chars == null) {
                throw new EndOfInput();
            }
            return new String(chars);
        }
        return prompt(label);
    }

    public boolean confirm(String question) {
        String answer = prompt(question + " " + dim("[y/N]") + " ").toLowerCase();
        return answer.equals("y") || answer.equals("yes");
    }

    /** Prompts until the user enters an integer in range, or blank when {@code allowBlank}. */
    public Integer promptInt(String label, int min, int max, boolean allowBlank) {
        while (true) {
            String raw = prompt(label);
            if (raw.isEmpty() && allowBlank) {
                return null;
            }
            try {
                int v = Integer.parseInt(raw);
                if (v >= min && v <= max) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // fall through to the hint
            }
            error("Enter a number from " + min + " to " + max + ".");
        }
    }

    public void error(String message) { println(red("✗ " + message)); }
    public void success(String message) { println(green("✓ " + message)); }
    public void info(String message) { println(dim(message)); }

    public void heading(String title) {
        println();
        println(bold(accent(title)));
        println(dim("─".repeat(Math.max(24, title.length()))));
    }

    public String bold(String s) { return style("1", s); }
    public String dim(String s) { return style("2", s); }
    public String red(String s) { return style("31", s); }
    public String green(String s) { return style("32", s); }
    public String yellow(String s) { return style("33", s); }
    public String accent(String s) { return style("36", s); }
    public String inverse(String s) { return style("7", s); }

    /** Position badge colours, matching the web app's colour coding. */
    public String position(String pos) {
        String padded = String.format("%-3s", pos);
        return switch (pos) {
            case "QB" -> style("31", padded);
            case "RB" -> style("32", padded);
            case "WR" -> style("34", padded);
            case "TE" -> style("33", padded);
            case "K" -> style("35", padded);
            default -> style("37", padded);
        };
    }

    private String style(String code, String s) {
        return color ? "\u001B[" + code + "m" + s + RESET : s;
    }
}
