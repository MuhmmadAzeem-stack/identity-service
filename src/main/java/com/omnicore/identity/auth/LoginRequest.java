package com.omnicore.identity.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank
    @Email
    @JsonAlias("email")
    String usernameOrEmail,
    @NotBlank String password
) {
}
