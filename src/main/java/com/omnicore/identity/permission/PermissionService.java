package com.omnicore.identity.permission;

import com.omnicore.identity.common.MessageResolver;
import com.omnicore.identity.common.PageResponse;
import com.omnicore.identity.common.constants.MessageKeys;
import com.omnicore.identity.common.constants.PaginationDefaults;
import com.omnicore.identity.permission.dto.CreatePermissionRequest;
import com.omnicore.identity.permission.dto.PermissionDetailResponse;
import com.omnicore.identity.permission.dto.PermissionListQuery;
import com.omnicore.identity.permission.dto.PermissionListResponse;
import com.omnicore.identity.permission.dto.ReplacePermissionDependenciesRequest;
import com.omnicore.identity.permission.dto.UpdatePermissionRequest;
import com.omnicore.identity.role.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionDependencyRepository permissionDependencyRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionMapper permissionMapper;
    private final PermissionValidator permissionValidator;
    private final MessageResolver messageResolver;

    @Transactional(readOnly = true)
    public PageResponse<PermissionListResponse> listPermissions(PermissionListQuery query) {
        String keyword = normalizeKeyword(query.keyword());
        String module = normalizeFilterToken(query.module(), permissionValidator::normalizeModule);
        String action = normalizeFilterToken(query.action(), permissionValidator::normalizeAction);

        int page = resolvePage(query.page());
        int size = resolveSize(query.size());
        String sortBy = resolveSortBy(query.sortBy());
        Sort.Direction sortDirection = resolveSortDirection(query.sortDirection());

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Specification<Permission> specification = PermissionSpecification.withFilters(
            keyword,
            module,
            action,
            query.isActive(),
            query.isSystem()
        );

        Page<Permission> permissionPage = permissionRepository.findAll(specification, pageable);
        Page<PermissionListResponse> responsePage = permissionPage.map(permission ->
            permissionMapper.toListResponse(
                permission,
                permissionDependencyRepository.countByPermissionId(permission.getId()),
                rolePermissionRepository.countByPermissionId(permission.getId())
            )
        );

        return PageResponse.from(responsePage);
    }

    public PermissionDetailResponse getPermissionDetail(Long id) {
        throw new UnsupportedOperationException(messageResolver.resolve(MessageKeys.NOT_IMPLEMENTED_YET));
    }

    public PermissionDetailResponse createPermission(CreatePermissionRequest request, Long currentUserId) {
        throw new UnsupportedOperationException(messageResolver.resolve(MessageKeys.NOT_IMPLEMENTED_YET));
    }

    public PermissionDetailResponse updatePermission(
        Long id,
        UpdatePermissionRequest request,
        Long currentUserId
    ) {
        throw new UnsupportedOperationException(messageResolver.resolve(MessageKeys.NOT_IMPLEMENTED_YET));
    }

    public void deletePermission(Long id, Long currentUserId) {
        throw new UnsupportedOperationException(messageResolver.resolve(MessageKeys.NOT_IMPLEMENTED_YET));
    }

    public PermissionDetailResponse activatePermission(Long id, Long currentUserId) {
        throw new UnsupportedOperationException(messageResolver.resolve(MessageKeys.NOT_IMPLEMENTED_YET));
    }

    public PermissionDetailResponse replaceDependencies(
        Long id,
        ReplacePermissionDependenciesRequest request,
        Long currentUserId
    ) {
        throw new UnsupportedOperationException(messageResolver.resolve(MessageKeys.NOT_IMPLEMENTED_YET));
    }

    public void removeDependency(Long id, Long dependencyId, Long currentUserId) {
        throw new UnsupportedOperationException(messageResolver.resolve(MessageKeys.NOT_IMPLEMENTED_YET));
    }

    private String normalizeKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return keyword.trim();
    }

    private String normalizeFilterToken(String value, java.util.function.Function<String, String> normalizer) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return normalizer.apply(value);
    }

    private int resolvePage(int page) {
        return Math.max(page, PaginationDefaults.DEFAULT_PAGE_NUMBER);
    }

    private int resolveSize(int size) {
        if (size < 1) {
            return PaginationDefaults.DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, PaginationDefaults.MAX_PAGE_SIZE);
    }

    private String resolveSortBy(String sortBy) {
        if (!StringUtils.hasText(sortBy)
            || !PermissionSearchConstants.ALLOWED_SORT_FIELDS.contains(sortBy)) {
            return PermissionSearchConstants.DEFAULT_SORT_BY;
        }
        return sortBy;
    }

    private Sort.Direction resolveSortDirection(String sortDirection) {
        if (!StringUtils.hasText(sortDirection)) {
            return PermissionSearchConstants.DEFAULT_SORT_DIRECTION;
        }
        try {
            return Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException ex) {
            return PermissionSearchConstants.DEFAULT_SORT_DIRECTION;
        }
    }
}
