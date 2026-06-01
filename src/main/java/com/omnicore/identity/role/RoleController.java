package com.omnicore.identity.role;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.omnicore.identity.common.ApiResponse;
import com.omnicore.identity.common.MessageResolver;
import com.omnicore.identity.common.PageResponse;
import com.omnicore.identity.common.SecurityUtils;
import com.omnicore.identity.common.constants.ApiPaths;
import com.omnicore.identity.common.constants.MessageKeys;
import com.omnicore.identity.common.constants.PaginationDefaults;
import com.omnicore.identity.role.dto.CreateRoleRequest;
import com.omnicore.identity.role.dto.ReplaceRolePermissionsRequest;
import com.omnicore.identity.role.dto.ReplaceUserRolesRequest;
import com.omnicore.identity.role.dto.RoleCreateResult;
import com.omnicore.identity.role.dto.RoleDetailResponse;
import com.omnicore.identity.role.dto.RoleListQuery;
import com.omnicore.identity.role.dto.RoleListResponse;
import com.omnicore.identity.role.dto.UpdateRoleRequest;
import com.omnicore.identity.role.dto.UserRoleAssignmentResponse;
import com.omnicore.identity.security.SecurityExpressions;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.ROLES_BASE_PATH)
@RequiredArgsConstructor
public class RoleController {

  private final RoleService roleService;
  private final MessageResolver messageResolver;
  private final SecurityUtils securityUtils;

  @GetMapping
  @PreAuthorize(SecurityExpressions.HAS_LIST_ROLE)
  public ApiResponse<PageResponse<RoleListResponse>> listRoles(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Boolean isActive,
      @RequestParam(required = false) Boolean isSystem,
      @RequestParam(defaultValue = PaginationDefaults.DEFAULT_PAGE_NUMBER_VALUE) int page,
      @RequestParam(defaultValue = PaginationDefaults.DEFAULT_PAGE_SIZE_VALUE) int size,
      @RequestParam(defaultValue = RoleSearchConstants.DEFAULT_SORT_BY) String sortBy,
      @RequestParam(defaultValue = RoleSearchConstants.DEFAULT_SORT_DIRECTION_VALUE)
          String sortDirection) {

    RoleListQuery query =
        new RoleListQuery(keyword, isActive, isSystem, page, size, sortBy, sortDirection);
    PageResponse<RoleListResponse> data = roleService.listRoles(query);

    return ApiResponse.success(messageResolver.resolve(MessageKeys.ROLES_FETCHED), data);
  }

  @GetMapping("/{id}")
  @PreAuthorize(SecurityExpressions.HAS_VIEW_ROLE)
  public ApiResponse<RoleDetailResponse> getRoleDetail(@PathVariable Long id) {
    RoleDetailResponse data = roleService.getRoleDetail(id);
    return ApiResponse.success(messageResolver.resolve(MessageKeys.ROLE_FETCHED), data);
  }

  @PostMapping
  @PreAuthorize(SecurityExpressions.HAS_CREATE_ROLE)
  public ResponseEntity<ApiResponse<RoleDetailResponse>> createRole(
      @Valid @RequestBody CreateRoleRequest request) {
    RoleCreateResult result = roleService.createRole(request, securityUtils.getCurrentUserId());
    String message =
        result.reactivated()
            ? messageResolver.resolve(MessageKeys.ROLE_REACTIVATED)
            : messageResolver.resolve(MessageKeys.ROLE_CREATED);
    HttpStatus status = result.reactivated() ? HttpStatus.OK : HttpStatus.CREATED;
    return ResponseEntity.status(status).body(ApiResponse.success(message, result.role()));
  }

  @PutMapping("/{id}")
  @PreAuthorize(SecurityExpressions.HAS_UPDATE_ROLE)
  public ApiResponse<RoleDetailResponse> updateRole(
      @PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
    RoleDetailResponse data = roleService.updateRole(id, request, securityUtils.getCurrentUserId());
    return ApiResponse.success(messageResolver.resolve(MessageKeys.ROLE_UPDATED), data);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize(SecurityExpressions.HAS_DELETE_ROLE)
  public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id, securityUtils.getCurrentUserId());
    return ResponseEntity.ok(
        ApiResponse.success(messageResolver.resolve(MessageKeys.ROLE_DELETED), null));
  }

  @PutMapping("/{id}/permissions")
  @PreAuthorize(SecurityExpressions.HAS_ASSIGN_ROLE_PERMISSION)
  public ApiResponse<RoleDetailResponse> replaceRolePermissions(
      @PathVariable Long id, @Valid @RequestBody ReplaceRolePermissionsRequest request) {
    RoleDetailResponse data =
        roleService.replaceRolePermissions(id, request, securityUtils.getCurrentUserId());
    return ApiResponse.success(messageResolver.resolve(MessageKeys.ROLE_PERMISSIONS_UPDATED), data);
  }

  @PostMapping("/{roleId}/users/{userId}")
  @PreAuthorize(SecurityExpressions.HAS_ASSIGN_USER_ROLE)
  public ApiResponse<UserRoleAssignmentResponse> assignRoleToUser(
      @PathVariable Long roleId, @PathVariable Long userId) {
    UserRoleAssignmentResponse data =
        roleService.assignRoleToUser(roleId, userId, securityUtils.getCurrentUserId());
    return ApiResponse.success(messageResolver.resolve(MessageKeys.USER_ROLE_ASSIGNED), data);
  }

  @DeleteMapping("/{roleId}/users/{userId}")
  @PreAuthorize(SecurityExpressions.HAS_REMOVE_USER_ROLE)
  public ApiResponse<UserRoleAssignmentResponse> removeRoleFromUser(
      @PathVariable Long roleId, @PathVariable Long userId) {
    UserRoleAssignmentResponse data =
        roleService.removeRoleFromUser(roleId, userId, securityUtils.getCurrentUserId());
    return ApiResponse.success(messageResolver.resolve(MessageKeys.USER_ROLE_REMOVED), data);
  }

  @PutMapping("/users/{userId}")
  @PreAuthorize(SecurityExpressions.HAS_ASSIGN_USER_ROLE)
  public ApiResponse<UserRoleAssignmentResponse> replaceUserRoles(
      @PathVariable Long userId, @Valid @RequestBody ReplaceUserRolesRequest request) {
    UserRoleAssignmentResponse data =
        roleService.replaceUserRoles(userId, request, securityUtils.getCurrentUserId());
    return ApiResponse.success(messageResolver.resolve(MessageKeys.USER_ROLES_UPDATED), data);
  }
}
