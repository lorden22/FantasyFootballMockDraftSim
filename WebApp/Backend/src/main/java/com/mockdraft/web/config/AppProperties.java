package com.mockdraft.web.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Application settings ({@code app.*}).
 *
 * @param baseUrl        public origin used to build links in emails; never derived from request headers
 * @param secureCookies  mark cookies Secure (keep true; browsers accept Secure cookies on http://localhost)
 * @param sessionTtl     lifetime of a sign-in session
 * @param resetTtl       lifetime of a password reset link
 * @param bcryptStrength BCrypt work factor
 * @param allowedOrigins extra origins allowed for CORS (empty = same-origin only)
 * @param mail           outgoing email settings
 */
@ConfigurationProperties("app")
public record AppProperties(
        String baseUrl,
        @DefaultValue("true") boolean secureCookies,
        @DefaultValue("7d") Duration sessionTtl,
        @DefaultValue("30m") Duration resetTtl,
        @DefaultValue("12") int bcryptStrength,
        @DefaultValue List<String> allowedOrigins,
        @DefaultValue Mail mail) {

    /**
     * @param from         From address for outgoing mail
     * @param logResetLinks log reset links instead of failing when SMTP is not configured (development only)
     */
    public record Mail(@DefaultValue("no-reply@localhost") String from, @DefaultValue("false") boolean logResetLinks) {}
}
