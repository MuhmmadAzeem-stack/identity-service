package com.omnicore.identity.role.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record ReplaceRolePermissionsRequest(@NotNull List<Long> permissionIds) {}
