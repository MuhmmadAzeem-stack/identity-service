package com.omnicore.identity.permission.dto;

import com.omnicore.identity.role.dto.RoleSummaryResponse;

import java.time.Instant;
import java.util.List;

public record PermissionDetailResponse(
    Long id,
    String name,
    String module,
    String action,
    String description,
    boolean active,
    boolean system,
    Instant createdAt,
    Long createdBy,
    Instant updatedAt,
    Long updatedBy,
    List<PermissionSummaryResponse> dependencies,
    List<PermissionSummaryResponse> usedAsDependencyBy,
    List<RoleSummaryResponse> usedByRoles
) {
}
