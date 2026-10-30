package com.mockdraft.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockdraft.core.model.User;
import com.mockdraft.core.service.AuthService;
import com.mockdraft.core.service.AuthenticationException;
import com.mockdraft.web.api.Dtos.EmailRequest;
import com.mockdraft.web.api.Dtos.LoginRequest;
import com.mockdraft.web.api.Dtos.RegisterRequest;
import com.mockdraft.web.api.Dtos.ResetConfirmRequest;
import com.mockdraft.web.api.Dtos.TokenRequest;
import com.mockdraft.web.api.Dtos.UserDto;
import com.mockdraft.web.api.Dtos.ValidDto;
import com.mockdraft.web.mail.PasswordResetMailer;
import com.mockdraft.web.security.RateLimits;
import com.mockdraft.web.security.SessionCookies;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;
    private final SessionCookies cookies;
    private final RateLimits rateLimits;
    private final PasswordResetMailer mailer;

    public AuthController(AuthService auth, SessionCookies cookies, RateLimits rateLimits, PasswordResetMailer mailer) {
        this.auth = auth;
        this.cookies = cookies;
        this.rateLimits = rateLimits;
        this.mailer = mailer;
    }

    /** Touching any endpoint issues the XSRF-TOKEN cookie; this one exists so the SPA can do it explicitly. */
    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf() {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest body, HttpServletRequest req, HttpServletResponse res) {
        rateLimits.register(req);
        User user = auth.register(body.username(), body.password(), body.email());
        cookies.write(res, auth.createSession(user.username()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.of(user));
    }

    @PostMapping("/login")
    public UserDto login(@RequestBody LoginRequest body, HttpServletRequest req, HttpServletResponse res) {
        rateLimits.login(req, body.username());
        User user = auth.authenticate(body.username(), body.password());
        rateLimits.loginSucceeded(req, body.username());
        // a fresh token on every sign-in, and any token the browser already held is revoked
        cookies.read(req).ifPresent(auth::logout);
        cookies.write(res, auth.createSession(user.username()));
        return UserDto.of(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req, HttpServletResponse res) {
        cookies.read(req).ifPresent(auth::logout);
        cookies.clear(res);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal String username) {
        if (username == null || "anonymous".equals(username)) {
            throw new AuthenticationException("Please sign in.");
        }
        return auth.currentUser(username).map(UserDto::of).orElseThrow(() -> new AuthenticationException("Please sign in."));
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> requestReset(@RequestBody EmailRequest body, HttpServletRequest req) {
        rateLimits.passwordResetRequest(req, body.email());
        // Same response whether or not the email exists; sending happens asynchronously so timing matches too.
        auth.requestPasswordReset(body.email()).ifPresent(mailer::send);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/password-reset/validate")
    public ValidDto validateReset(@RequestBody TokenRequest body, HttpServletRequest req) {
        rateLimits.passwordResetToken(req);
        return new ValidDto(auth.isResetTokenValid(body.token()));
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> confirmReset(@RequestBody ResetConfirmRequest body, HttpServletRequest req) {
        rateLimits.passwordResetToken(req);
        auth.resetPassword(body.token(), body.password());
        return ResponseEntity.noContent().build();
    }
}
