package com.omnicore.identity.permission.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReplacePermissionDependenciesRequest(
    @NotNull List<Long> dependencyIds
) {
}
