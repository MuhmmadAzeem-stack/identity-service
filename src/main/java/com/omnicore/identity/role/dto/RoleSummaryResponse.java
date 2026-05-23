package com.omnicore.identity.role.dto;

public record RoleSummaryResponse(
    Long id,
    String name,
    String description,
    boolean active,
    boolean system
) {
}
