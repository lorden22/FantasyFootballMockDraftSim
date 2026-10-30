package com.mockdraft.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mockdraft.core.service.AuthService;
import com.mockdraft.core.service.AuthenticationException;
import com.mockdraft.core.service.ConflictException;
import com.mockdraft.core.service.ValidationException;

class AuthServiceTest {

    private MockDraftCore core;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        core = TestDb.core();
        auth = core.auth();
    }

    @Test
    void registerAndAuthenticate() {
        auth.register("Bob_1", "a much better pass", "Bob@Example.com");
        assertThat(auth.authenticate("Bob_1", "a much better pass").email()).isEqualTo("bob@example.com");
        assertThat(auth.authenticate("bob_1", "a much better pass").username()).isEqualTo("Bob_1");
        assertThatThrownBy(() -> auth.authenticate("Bob_1", "wrong password")).isInstanceOf(AuthenticationException.class);
        assertThatThrownBy(() -> auth.authenticate("nobody", "a much better pass")).isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password.");
    }

    @Test
    void registrationRules() {
        auth.register("taken", "long enough pw", "a@b.co");
        assertThatThrownBy(() -> auth.register("TAKEN", "long enough pw", null)).isInstanceOf(ConflictException.class);
        assertThatThrownBy(() -> auth.register("other", "long enough pw", "A@B.co")).isInstanceOf(ConflictException.class);
        assertThatThrownBy(() -> auth.register("ab", "long enough pw", null)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> auth.register("Anonymous", "long enough pw", null)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> auth.register("bad name", "long enough pw", null)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> auth.register("short", "1234567", null)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> auth.register("common", "password123", null)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> auth.register("selfref", "xxselfrefxx", null)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> auth.register("toolong", "x".repeat(73), null)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> auth.register("bademail", "long enough pw", "not-an-email")).isInstanceOf(ValidationException.class);
    }

    @Test
    void sessionsResolveAndLogout() {
        auth.register("carol", "long enough pw", null);
        AuthService.Session s = auth.createSession("carol");
        assertThat(s.expiresAt()).isAfter(Instant.now());
        assertThat(auth.resolveSession(s.token())).contains("carol");
        assertThat(auth.resolveSession("garbage")).isEmpty();
        assertThat(auth.resolveSession(s.token().substring(1) + "A")).isEmpty();
        auth.logout(s.token());
        assertThat(auth.resolveSession(s.token())).isEmpty();
    }

    @Test
    void passwordResetFlowRevokesSessions() {
        auth.register("dave", "long enough pw", "dave@example.com");
        AuthService.Session session = auth.createSession("dave");
        assertThat(auth.requestPasswordReset("unknown@example.com")).isEmpty();

        AuthService.PasswordResetRequest req = auth.requestPasswordReset("DAVE@example.com").orElseThrow();
        assertThat(auth.isResetTokenValid(req.token())).isTrue();
        auth.resetPassword(req.token(), "brand new secret");

        assertThat(auth.isResetTokenValid(req.token())).isFalse();
        assertThat(auth.resolveSession(session.token())).isEmpty();
        assertThatThrownBy(() -> auth.resetPassword(req.token(), "another secret!")).isInstanceOf(ValidationException.class);
        assertThat(auth.authenticate("dave", "brand new secret").username()).isEqualTo("dave");
    }

    @Test
    void changePasswordKeepsCurrentSessionOnly() {
        auth.register("erin", "long enough pw", null);
        AuthService.Session keep = auth.createSession("erin");
        AuthService.Session other = auth.createSession("erin");
        assertThatThrownBy(() -> auth.changePassword("erin", "wrong", "new long secret", keep.token()))
                .isInstanceOf(ValidationException.class);
        auth.changePassword("erin", "long enough pw", "new long secret", keep.token());
        assertThat(auth.resolveSession(keep.token())).contains("erin");
        assertThat(auth.resolveSession(other.token())).isEmpty();
    }

    @Test
    void deleteAccountCascadesDrafts() {
        auth.register("frank", "long enough pw", null);
        core.drafts().create("frank", "Team", 6, 1);
        auth.deleteAccount("frank", "long enough pw");
        assertThatThrownBy(() -> auth.authenticate("frank", "long enough pw")).isInstanceOf(AuthenticationException.class);
        assertThat(core.database().queryLong("SELECT COUNT(*) FROM draft_teams")).isZero();
    }
}
