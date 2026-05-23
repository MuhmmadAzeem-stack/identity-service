package com.omnicore.identity.permission.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdatePermissionRequest(
    @Size(max = 150) String name,
    @Size(max = 100) String module,
    @Size(max = 50) String action,
    @Size(max = 500) String description,
    List<Long> dependencyIds
) {
}
