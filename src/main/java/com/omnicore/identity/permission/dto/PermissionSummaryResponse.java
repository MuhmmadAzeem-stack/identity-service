package com.omnicore.identity.permission.dto;

public record PermissionSummaryResponse(
    Long id,
    String name,
    String module,
    String action,
    String description,
    boolean active,
    boolean system) {}
