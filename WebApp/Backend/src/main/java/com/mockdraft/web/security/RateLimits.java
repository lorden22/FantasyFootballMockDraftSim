package com.mockdraft.web.security;

import java.time.Clock;
import java.time.Duration;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.mockdraft.core.service.RateLimitedException;
import com.mockdraft.core.util.RateLimiter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Per-client throttles for sensitive endpoints. Client IP comes from {@link HttpServletRequest#getRemoteAddr()},
 * which honours X-Forwarded-For only from trusted proxies (see server.forward-headers-strategy).
 */
@Component
public class RateLimits {

    private final Clock clock = Clock.systemUTC();
    private final RateLimiter loginPerAccount = new RateLimiter(5, Duration.ofMinutes(1), clock);
    private final RateLimiter loginPerIp = new RateLimiter(20, Duration.ofMinutes(1), clock);
    private final RateLimiter registerPerIp = new RateLimiter(5, Duration.ofMinutes(10), clock);
    private final RateLimiter resetRequestPerIp = new RateLimiter(5, Duration.ofMinutes(15), clock);
    private final RateLimiter resetRequestPerEmail = new RateLimiter(3, Duration.ofHours(1), clock);
    private final RateLimiter resetTokenPerIp = new RateLimiter(20, Duration.ofMinutes(10), clock);
    private final RateLimiter accountPerUser = new RateLimiter(10, Duration.ofMinutes(10), clock);
    private final RateLimiter draftWritesPerUser = new RateLimiter(120, Duration.ofMinutes(1), clock);

    public void login(HttpServletRequest req, String username) {
        String ip = req.getRemoteAddr();
        check(loginPerIp, ip);
        check(loginPerAccount, ip + "|" + normalize(username));
    }

    public void loginSucceeded(HttpServletRequest req, String username) {
        loginPerAccount.reset(req.getRemoteAddr() + "|" + normalize(username));
    }

    public void register(HttpServletRequest req) {
        check(registerPerIp, req.getRemoteAddr());
    }

    public void passwordResetRequest(HttpServletRequest req, String email) {
        check(resetRequestPerIp, req.getRemoteAddr());
        check(resetRequestPerEmail, normalize(email));
    }

    public void passwordResetToken(HttpServletRequest req) {
        check(resetTokenPerIp, req.getRemoteAddr());
    }

    public void accountChange(String username) {
        check(accountPerUser, normalize(username));
    }

    public void draftWrite(String username) {
        check(draftWritesPerUser, normalize(username));
    }

    private static void check(RateLimiter limiter, String key) {
        if (!limiter.tryAcquire(key)) {
            throw new RateLimitedException("Too many attempts. Please wait a minute and try again.");
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
