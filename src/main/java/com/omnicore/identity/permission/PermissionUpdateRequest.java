package com.omnicore.identity.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PermissionUpdateRequest(
        @NotBlank String name,
        @NotBlank String module,
        @NotBlank String action,
        String description,
        @NotNull Boolean active
) {
}
