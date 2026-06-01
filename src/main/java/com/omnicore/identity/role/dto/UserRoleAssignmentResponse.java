package com.omnicore.identity.role.dto;

import java.util.List;

public record UserRoleAssignmentResponse(
    Long userId, String email, List<RoleSummaryResponse> roles, int tokenVersion) {}
