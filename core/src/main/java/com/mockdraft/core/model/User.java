package com.mockdraft.core.model;

import java.time.Instant;

/** Public view of an account (never carries credentials). */
public record User(String username, String email, Instant createdAt) {}
