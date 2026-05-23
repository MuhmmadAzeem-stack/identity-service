package com.omnicore.identity.auth.dto;

import java.util.List;

public record MeResponse(
    String email,
    AuthUserResponse user,
    List<String> roles,
    List<String> permissions,
    int tokenVersion
) {
}
