package com.omnicore.identity.permission;

import jakarta.validation.constraints.NotBlank;

public record PermissionUpdateRequest(
        @NotBlank String name,
        @NotBlank String resource,
        @NotBlank String action,
        String description,
        boolean active
) {
}