package com.mockdraft.web.security;

import java.time.Duration;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.mockdraft.core.service.AuthService;
import com.mockdraft.web.config.AppProperties;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Reads and writes the HttpOnly session cookie. JavaScript can never see the session token. */
@Component
public class SessionCookies {

    public static final String NAME = "md_session";

    private final AppProperties props;

    public SessionCookies(AppProperties props) {
        this.props = props;
    }

    public Optional<String> read(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        for (Cookie c : cookies) {
            if (NAME.equals(c.getName())) {
                return Optional.ofNullable(c.getValue());
            }
        }
        return Optional.empty();
    }

    public void write(HttpServletResponse response, AuthService.Session session) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie(session.token(), props.sessionTtl()).toString());
    }

    public void clear(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie("", Duration.ZERO).toString());
    }

    private ResponseCookie cookie(String value, Duration maxAge) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(props.secureCookies())
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
