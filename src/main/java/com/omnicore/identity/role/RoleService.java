package com.omnicore.identity.role;

import com.omnicore.identity.common.PageResponse;
import com.omnicore.identity.role.dto.CreateRoleRequest;
import com.omnicore.identity.role.dto.ReplaceRolePermissionsRequest;
import com.omnicore.identity.role.dto.ReplaceUserRolesRequest;
import com.omnicore.identity.role.dto.RoleCreateResult;
import com.omnicore.identity.role.dto.RoleDetailResponse;
import com.omnicore.identity.role.dto.RoleListQuery;
import com.omnicore.identity.role.dto.RoleListResponse;
import com.omnicore.identity.role.dto.UpdateRoleRequest;
import com.omnicore.identity.role.dto.UserRoleAssignmentResponse;

public interface RoleService {

  PageResponse<RoleListResponse> listRoles(RoleListQuery query);

  RoleDetailResponse getRoleDetail(Long id);

  RoleCreateResult createRole(CreateRoleRequest request, Long currentUserId);

  RoleDetailResponse updateRole(Long id, UpdateRoleRequest request, Long currentUserId);

  void deleteRole(Long id, Long currentUserId);

  RoleDetailResponse replaceRolePermissions(
      Long roleId, ReplaceRolePermissionsRequest request, Long currentUserId);

  UserRoleAssignmentResponse assignRoleToUser(Long roleId, Long userId, Long currentUserId);

  UserRoleAssignmentResponse removeRoleFromUser(Long roleId, Long userId, Long currentUserId);

  UserRoleAssignmentResponse replaceUserRoles(
      Long userId, ReplaceUserRolesRequest request, Long currentUserId);
}
