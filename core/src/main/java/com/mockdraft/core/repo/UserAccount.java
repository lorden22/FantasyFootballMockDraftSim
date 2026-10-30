package com.mockdraft.core.repo;

import java.time.Instant;

import com.mockdraft.core.model.User;

/** Stored account including credentials. Never leaves the core module. */
public record UserAccount(String username, String email, String passwordHash, String legacySalt, Instant createdAt) {

    public User toUser() {
        return new User(username, email, createdAt);
    }
}
