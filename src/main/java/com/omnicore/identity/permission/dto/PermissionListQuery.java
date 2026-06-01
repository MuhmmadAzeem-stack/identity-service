package com.omnicore.identity.permission.dto;

public record PermissionListQuery(
    String keyword,
    String module,
    String action,
    Boolean isActive,
    Boolean isSystem,
    int page,
    int size,
    String sortBy,
    String sortDirection) {}
