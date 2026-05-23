package com.omnicore.identity.permission;

public record PermissionResponse(
        Long id,
        String name,
        String module,
        String action,
        String description,
        boolean active,
        boolean system
) {
}
