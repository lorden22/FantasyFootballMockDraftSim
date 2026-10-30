package com.mockdraft.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockdraft.core.service.AuthService;
import com.mockdraft.web.api.Dtos.ChangeEmailRequest;
import com.mockdraft.web.api.Dtos.ChangePasswordRequest;
import com.mockdraft.web.api.Dtos.PasswordRequest;
import com.mockdraft.web.api.Dtos.UserDto;
import com.mockdraft.web.security.RateLimits;
import com.mockdraft.web.security.SessionCookies;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Account self-service. Every change re-checks the current password. */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AuthService auth;
    private final SessionCookies cookies;
    private final RateLimits rateLimits;

    public AccountController(AuthService auth, SessionCookies cookies, RateLimits rateLimits) {
        this.auth = auth;
        this.cookies = cookies;
        this.rateLimits = rateLimits;
    }

    @PutMapping("/email")
    public UserDto changeEmail(@AuthenticationPrincipal String username, @RequestBody ChangeEmailRequest body) {
        rateLimits.accountChange(username);
        return UserDto.of(auth.changeEmail(username, body.currentPassword(), body.email()));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal String username, @RequestBody ChangePasswordRequest body,
            HttpServletRequest req) {
        rateLimits.accountChange(username);
        auth.changePassword(username, body.currentPassword(), body.newPassword(), cookies.read(req).orElse(null));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal String username, @RequestBody PasswordRequest body,
            HttpServletResponse res) {
        rateLimits.accountChange(username);
        auth.deleteAccount(username, body.password());
        cookies.clear(res);
        return ResponseEntity.noContent().build();
    }
}
