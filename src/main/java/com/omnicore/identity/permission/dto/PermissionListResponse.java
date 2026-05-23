package com.omnicore.identity.permission.dto;

import java.time.Instant;

public record PermissionListResponse(
    Long id,
    String name,
    String module,
    String action,
    String description,
    boolean active,
    boolean system,
    long dependencyCount,
    long roleCount,
    Instant createdAt,
    Instant updatedAt
) {
}
