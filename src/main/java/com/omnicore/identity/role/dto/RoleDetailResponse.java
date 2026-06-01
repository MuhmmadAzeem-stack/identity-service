package com.omnicore.identity.role.dto;

import java.time.Instant;
import java.util.List;

import com.omnicore.identity.permission.dto.PermissionSummaryResponse;

public record RoleDetailResponse(
    Long id,
    String name,
    String description,
    boolean active,
    boolean system,
    Instant createdAt,
    Long createdBy,
    Instant updatedAt,
    Long updatedBy,
    List<PermissionSummaryResponse> permissions,
    long assignedUserCount) {}
