package com.omnicore.identity.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePermissionRequest(
    @NotBlank @Size(max = 150) String name,
    @NotBlank @Size(max = 100) String module,
    @NotBlank @Size(max = 50) String action,
    @Size(max = 500) String description,
    List<Long> dependencyIds
) {
}
