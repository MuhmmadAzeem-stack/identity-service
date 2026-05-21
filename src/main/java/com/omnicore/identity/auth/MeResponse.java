package com.omnicore.identity.auth;

import java.time.Instant;
import java.util.List;

public record MeResponse(
        String id,
        String username,
        String email,
        List<String> roles,
        List<String> permissions,
        Instant issuedAt,
        Instant expiresAt
) {
}