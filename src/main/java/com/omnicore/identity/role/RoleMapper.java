package com.omnicore.identity.role;

import java.util.List;

import org.springframework.stereotype.Component;

import com.omnicore.identity.permission.Permission;
import com.omnicore.identity.permission.dto.PermissionSummaryResponse;
import com.omnicore.identity.role.dto.RoleDetailResponse;
import com.omnicore.identity.role.dto.RoleListResponse;
import com.omnicore.identity.role.dto.RoleSummaryResponse;
import com.omnicore.identity.role.dto.UserRoleAssignmentResponse;
import com.omnicore.identity.user.User;

@Component
public class RoleMapper {

  public RoleSummaryResponse toSummary(Role role) {
    return new RoleSummaryResponse(
        role.getId(), role.getName(), role.getDescription(), role.isActive(), role.isSystem());
  }

  public PermissionSummaryResponse toPermissionSummary(Permission permission) {
    return new PermissionSummaryResponse(
        permission.getId(),
        permission.getName(),
        permission.getModule(),
        permission.getAction(),
        permission.getDescription(),
        permission.isActive(),
        permission.isSystem());
  }

  public RoleListResponse toListResponse(Role role, long permissionCount, long userCount) {
    return new RoleListResponse(
        role.getId(),
        role.getName(),
        role.getDescription(),
        role.isActive(),
        role.isSystem(),
        permissionCount,
        userCount,
        role.getCreatedAt(),
        role.getUpdatedAt());
  }

  public RoleDetailResponse toDetailResponse(
      Role role, List<PermissionSummaryResponse> permissions, long assignedUserCount) {
    return new RoleDetailResponse(
        role.getId(),
        role.getName(),
        role.getDescription(),
        role.isActive(),
        role.isSystem(),
        role.getCreatedAt(),
        role.getCreatedBy(),
        role.getUpdatedAt(),
        role.getUpdatedBy(),
        permissions,
        assignedUserCount);
  }

  public UserRoleAssignmentResponse toUserRoleAssignmentResponse(User user) {
    List<RoleSummaryResponse> roles =
        user.getRoles().stream()
            .filter(role -> role.isActive() && !role.isDeleted())
            .map(this::toSummary)
            .sorted(java.util.Comparator.comparing(RoleSummaryResponse::name))
            .toList();

    return new UserRoleAssignmentResponse(
        user.getId(), user.getEmail(), roles, user.getTokenVersion());
  }
}
