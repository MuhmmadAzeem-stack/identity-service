package com.omnicore.identity.permission;

import jakarta.validation.constraints.NotBlank;

public record PermissionCreateRequest(
        @NotBlank String name,
        @NotBlank String module,
        @NotBlank String action,
        String description
) {
}
