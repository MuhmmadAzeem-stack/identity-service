package com.omnicore.identity.auth;

import com.omnicore.identity.common.constants.JwtClaims;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank
    @Email
    @JsonAlias(JwtClaims.CLAIM_EMAIL)
    String usernameOrEmail,
    @NotBlank String password
) {
}
