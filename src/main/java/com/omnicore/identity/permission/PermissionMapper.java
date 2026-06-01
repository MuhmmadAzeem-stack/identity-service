package com.omnicore.identity.permission;

import java.util.List;

import org.springframework.stereotype.Component;

import com.omnicore.identity.permission.dto.PermissionDetailResponse;
import com.omnicore.identity.permission.dto.PermissionListResponse;
import com.omnicore.identity.permission.dto.PermissionSummaryResponse;
import com.omnicore.identity.role.Role;
import com.omnicore.identity.role.dto.RoleSummaryResponse;

@Component
public class PermissionMapper {

  public PermissionSummaryResponse toSummary(Permission permission) {
    return new PermissionSummaryResponse(
        permission.getId(),
        permission.getName(),
        permission.getModule(),
        permission.getAction(),
        permission.getDescription(),
        permission.isActive(),
        permission.isSystem());
  }

  public RoleSummaryResponse toRoleSummary(Role role) {
    return new RoleSummaryResponse(
        role.getId(), role.getName(), role.getDescription(), role.isActive(), role.isSystem());
  }

  public PermissionListResponse toListResponse(
      Permission permission, long dependencyCount, long roleCount) {
    return new PermissionListResponse(
        permission.getId(),
        permission.getName(),
        permission.getModule(),
        permission.getAction(),
        permission.getDescription(),
        permission.isActive(),
        permission.isSystem(),
        dependencyCount,
        roleCount,
        permission.getCreatedAt(),
        permission.getUpdatedAt());
  }

  public PermissionDetailResponse toDetailResponse(
      Permission permission,
      List<PermissionSummaryResponse> dependencies,
      List<PermissionSummaryResponse> usedAsDependencyBy,
      List<RoleSummaryResponse> usedByRoles) {
    return new PermissionDetailResponse(
        permission.getId(),
        permission.getName(),
        permission.getModule(),
        permission.getAction(),
        permission.getDescription(),
        permission.isActive(),
        permission.isSystem(),
        permission.getCreatedAt(),
        permission.getCreatedBy(),
        permission.getUpdatedAt(),
        permission.getUpdatedBy(),
        dependencies,
        usedAsDependencyBy,
        usedByRoles);
  }
}
