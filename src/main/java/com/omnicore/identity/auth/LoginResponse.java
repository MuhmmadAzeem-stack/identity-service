package com.omnicore.identity.auth;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {
}