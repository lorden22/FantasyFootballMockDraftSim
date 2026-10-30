package com.mockdraft.core.service;

/** Requested resource does not exist (or belongs to another user). */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
