package com.omnicore.identity.role.dto;

import java.time.Instant;

public record RoleListResponse(
    Long id,
    String name,
    String description,
    boolean active,
    boolean system,
    long permissionCount,
    long userCount,
    Instant createdAt,
    Instant updatedAt) {}
