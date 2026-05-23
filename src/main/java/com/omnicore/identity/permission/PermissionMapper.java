package com.omnicore.identity.permission;

import com.omnicore.identity.permission.dto.PermissionDetailResponse;
import com.omnicore.identity.permission.dto.PermissionListResponse;
import com.omnicore.identity.permission.dto.PermissionSummaryResponse;
import com.omnicore.identity.permission.dto.RoleSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

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
            permission.isSystem()
        );
    }

    public PermissionListResponse toListResponse(
        Permission permission,
        long dependencyCount,
        long roleCount
    ) {
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
            permission.getUpdatedAt()
        );
    }

    public PermissionDetailResponse toDetailResponse(
        Permission permission,
        List<PermissionSummaryResponse> dependencies,
        List<PermissionSummaryResponse> usedAsDependencyBy,
        List<RoleSummaryResponse> usedByRoles
    ) {
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
            usedByRoles
        );
    }
}
