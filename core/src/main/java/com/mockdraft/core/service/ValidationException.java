package com.mockdraft.core.service;

import java.util.Map;

/** Input failed validation. {@link #fields()} maps field name to a user-facing message. */
public class ValidationException extends RuntimeException {

    private final Map<String, String> fields;

    public ValidationException(String field, String message) {
        super(message);
        this.fields = Map.of(field, message);
    }

    public Map<String, String> fields() {
        return fields;
    }
}
