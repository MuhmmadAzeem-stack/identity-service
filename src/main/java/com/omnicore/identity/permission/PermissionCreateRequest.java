package com.omnicore.identity.permission;

import jakarta.validation.constraints.NotBlank;

public record PermissionCreateRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotBlank String resource,
        @NotBlank String action,
        String description
) {
}