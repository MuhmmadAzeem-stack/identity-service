package com.omnicore.identity.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.omnicore.identity.common.constants.JwtClaims;

public record LoginRequest(
    @NotBlank @Email @JsonAlias(JwtClaims.CLAIM_EMAIL) String usernameOrEmail,
    @NotBlank String password) {}
