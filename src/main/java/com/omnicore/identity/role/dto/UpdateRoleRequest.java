package com.omnicore.identity.role.dto;

import java.util.List;

import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(
    @Size(max = 150) String name, @Size(max = 500) String description, List<Long> permissionIds) {}
