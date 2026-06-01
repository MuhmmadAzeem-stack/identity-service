package com.omnicore.identity.permission.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record ReplacePermissionDependenciesRequest(@NotNull List<Long> dependencyIds) {}
