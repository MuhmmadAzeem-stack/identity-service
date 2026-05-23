package com.omnicore.identity.permission;

import com.omnicore.identity.common.ApiResponse;
import com.omnicore.identity.common.MessageResolver;
import com.omnicore.identity.common.PageResponse;
import com.omnicore.identity.common.constants.ApiPaths;
import com.omnicore.identity.common.constants.MessageKeys;
import com.omnicore.identity.common.constants.PaginationDefaults;
import com.omnicore.identity.permission.dto.PermissionDetailResponse;
import com.omnicore.identity.permission.dto.PermissionListQuery;
import com.omnicore.identity.permission.dto.PermissionListResponse;
import com.omnicore.identity.security.SecurityExpressions;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.PERMISSIONS_BASE_PATH)
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final MessageResolver messageResolver;

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
        @RequestParam(defaultValue = PermissionSearchConstants.DEFAULT_SORT_DIRECTION_VALUE) String sortDirection
    ) {
        PermissionListQuery query = new PermissionListQuery(
            keyword,
            module,
            action,
            isActive,
            isSystem,
            page,
            size,
            sortBy,
            sortDirection
        );

        PageResponse<PermissionListResponse> data = permissionService.listPermissions(query);
        return ApiResponse.success(messageResolver.resolve(MessageKeys.PERMISSIONS_FETCHED), data);
    }

    @GetMapping("/{id}")
    @PreAuthorize(SecurityExpressions.HAS_VIEW_PERMISSION)
    public ApiResponse<PermissionDetailResponse> getPermissionDetail(@PathVariable Long id) {
        PermissionDetailResponse data = permissionService.getPermissionDetail(id);
        return ApiResponse.success(messageResolver.resolve(MessageKeys.PERMISSION_FETCHED), data);
    }
}
