package com.mockdraft.console;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.mockdraft.core.MockDraftCore;

class ConsoleAppTest {

    private static MockDraftCore core() {
        String url = "jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=MariaDB;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;IGNORECASE=TRUE;DB_CLOSE_DELAY=-1";
        return new MockDraftCore(new SimpleDataSource(url, "sa", ""), Clock.systemUTC(), 4,
                MockDraftCore.DEFAULT_SESSION_TTL, MockDraftCore.DEFAULT_RESET_TTL);
    }

    private static String run(MockDraftCore core, String input) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Terminal t = new Terminal(new BufferedReader(new StringReader(input)), new PrintStream(bytes, true, StandardCharsets.UTF_8),
                null, false);
        try {
            new ConsoleApp(t, core).run();
        } catch (Terminal.EndOfInput ignored) {
            // script ran out
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void registerDraftQuitResumeAndFinish() {
        MockDraftCore core = core();
        StringBuilder script = new StringBuilder("2\ncoach\nsecure draft pass\nsecure draft pass\n\n")
                .append("1\nUnderdogs\n8\n2\n")   // new 8-team draft from slot 2
                .append("f QB\n1\nq\n")            // draft the top QB, then leave
                .append("1\n");                   // resume
        script.append("a\n".repeat(14)).append("n\n0\n0\n");
        String out = run(core, script.toString());

        assertThat(out).contains("Account created").contains("You drafted").contains("(QB)")
                .contains("Draft saved").contains("Draft complete!").doesNotContain("✗");
        var drafts = core.drafts().list("coach");
        assertThat(drafts).hasSize(1);
        assertThat(drafts.get(0).isComplete()).isTrue();
        assertThat(drafts.get(0).picksMade()).isEqualTo(8 * 15);
    }

    @Test
    void accountCreatedInConsoleWorksForWebSignIn() {
        MockDraftCore core = core();
        run(core, "2\nshared_user\nsecure draft pass\nsecure draft pass\nme@example.com\n0\n0\n");
        // the web app authenticates through the same AuthService and tables
        assertThat(core.auth().authenticate("shared_user", "secure draft pass").email()).isEqualTo("me@example.com");
    }

    @Test
    void rejectsBadInputGracefully() {
        String out = run(core(), "2\nx\nshort\nshort\n\n1\nnobody\nwrong password\n0\n");
        assertThat(out).contains("Username must be").contains("Invalid username or password");
    }

    @Test
    void configDefaultsToEmbeddedDatabase() {
        ConsoleConfig cfg = ConsoleConfig.from(new String[0], Map.of("MOCKDRAFT_HOME", "/tmp/md"));
        assertThat(cfg.isEmbedded()).isTrue();
        assertThat(cfg.dbUrl()).startsWith("jdbc:h2:file:" + Path.of("/tmp/md/mockdraft").toAbsolutePath());

        ConsoleConfig maria = ConsoleConfig.from(new String[] {"--db-url", "jdbc:mariadb://db:3306/mockdraft", "--db-user", "md"},
                Map.of("MOCKDRAFT_DB_PASSWORD", "s3cret", "NO_COLOR", "1"));
        assertThat(maria.isEmbedded()).isFalse();
        assertThat(maria.dbPassword()).isEqualTo("s3cret");
        assertThat(maria.color()).isFalse();
    }
}
