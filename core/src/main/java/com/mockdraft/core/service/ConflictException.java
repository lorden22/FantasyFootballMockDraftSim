package com.mockdraft.core.service;

/** Request conflicts with the current state. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
