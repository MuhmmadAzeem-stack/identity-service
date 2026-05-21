package com.omnicore.identity.permission;

public record PermissionResponse(
        Long id,
        String code,
        String name,
        String resource,
        String action,
        String description,
        boolean active
) {
}