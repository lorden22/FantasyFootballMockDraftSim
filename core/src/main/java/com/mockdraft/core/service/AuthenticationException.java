package com.mockdraft.core.service;

/** Credentials are missing or invalid. */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
