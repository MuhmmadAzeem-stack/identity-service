package com.omnicore.identity.role.dto;

public record RoleListQuery(
    String keyword, Boolean isActive, Boolean isSystem, int page, int size, String sortBy,
    String sortDirection) {}
