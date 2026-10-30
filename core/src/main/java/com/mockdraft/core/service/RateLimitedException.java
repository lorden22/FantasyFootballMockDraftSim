package com.mockdraft.core.service;

/** Too many attempts; caller should wait. */
public class RateLimitedException extends RuntimeException {
    public RateLimitedException(String message) {
        super(message);
    }
}
