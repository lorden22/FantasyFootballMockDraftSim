package com.mockdraft.core.db;

/** A unique or primary key constraint was violated. */
public class DuplicateKeyException extends DataAccessException {
    public DuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
