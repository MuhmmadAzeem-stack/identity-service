package com.omnicore.identity.permission;

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
import com.omnicore.identity.permission.dto.*;
import com.omnicore.identity.security.SecurityExpressions;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.PERMISSIONS_BASE_PATH)
@RequiredArgsConstructor
public class PermissionController {

  private final PermissionService permissionService;

  private final MessageResolver messageResolver;

  private final SecurityUtils securityUtils;

  @GetMapping
  @PreAuthorize(SecurityExpressions.HAS_LIST_PERMISSION)
  public ApiResponse<PageResponse<PermissionListResponse>> listPermissions(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String module,
      @RequestParam(required = false) String action,
      @RequestParam(required = false) Boolean isActive,
      @RequestParam(required = false) Boolean isSystem,
      @RequestParam(defaultValue = PaginationDefaults.DEFAULT_PAGE_NUMBER_VALUE) int page,
      @RequestParam(defaultValue = PaginationDefaults.DEFAULT_PAGE_SIZE_VALUE) int size,
      @RequestParam(defaultValue = PermissionSearchConstants.DEFAULT_SORT_BY) String sortBy,
      @RequestParam(defaultValue = PermissionSearchConstants.DEFAULT_SORT_DIRECTION_VALUE)
          String sortDirection) {

    PermissionListQuery query =
        new PermissionListQuery(
            keyword, module, action, isActive, isSystem, page, size, sortBy, sortDirection);

    PageResponse<PermissionListResponse> data = permissionService.listPermissions(query);

    return ApiResponse.success(messageResolver.resolve(MessageKeys.PERMISSIONS_FETCHED), data);
  }

  @GetMapping("/{id}")
  @PreAuthorize(SecurityExpressions.HAS_VIEW_PERMISSION)
  public ApiResponse<PermissionDetailResponse> getPermissionDetail(@PathVariable Long id) {

    PermissionDetailResponse data = permissionService.getPermissionDetail(id);

    return ApiResponse.success(messageResolver.resolve(MessageKeys.PERMISSION_FETCHED), data);
  }

  @PostMapping
  @PreAuthorize(SecurityExpressions.HAS_CREATE_PERMISSION)
  public ResponseEntity<ApiResponse<PermissionDetailResponse>> createPermission(
      @Valid @RequestBody CreatePermissionRequest request) {

    PermissionCreateResult result =
        permissionService.createPermission(request, securityUtils.getCurrentUserId());

    String message =
        result.reactivated()
            ? messageResolver.resolve(MessageKeys.PERMISSION_REACTIVATED)
            : messageResolver.resolve(MessageKeys.PERMISSION_CREATED);

    HttpStatus status = result.reactivated() ? HttpStatus.OK : HttpStatus.CREATED;

    return ResponseEntity.status(status).body(ApiResponse.success(message, result.permission()));
  }

  @PutMapping("/{id}")
  @PreAuthorize(SecurityExpressions.HAS_UPDATE_PERMISSION)
  public ApiResponse<PermissionDetailResponse> updatePermission(
      @PathVariable Long id, @Valid @RequestBody UpdatePermissionRequest request) {

    PermissionDetailResponse data =
        permissionService.updatePermission(id, request, securityUtils.getCurrentUserId());

    return ApiResponse.success(messageResolver.resolve(MessageKeys.PERMISSION_UPDATED), data);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize(SecurityExpressions.HAS_DELETE_PERMISSION)
  public ApiResponse<Void> deletePermission(@PathVariable Long id) {
    permissionService.deletePermission(id, securityUtils.getCurrentUserId());

    return ApiResponse.success(messageResolver.resolve(MessageKeys.PERMISSION_DELETED), null);
  }

  @PutMapping("/{id}/activate")
  @PreAuthorize(SecurityExpressions.HAS_UPDATE_PERMISSION)
  public ApiResponse<PermissionDetailResponse> activatePermission(@PathVariable Long id) {
    PermissionDetailResponse data =
        permissionService.activatePermission(id, securityUtils.getCurrentUserId());

    return ApiResponse.success(messageResolver.resolve(MessageKeys.PERMISSION_ACTIVATED), data);
  }
}
