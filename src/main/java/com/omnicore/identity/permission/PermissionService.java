package com.omnicore.identity.permission;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.omnicore.identity.common.MessageResolver;
import com.omnicore.identity.common.PageResponse;
import com.omnicore.identity.common.SecurityUtils;
import com.omnicore.identity.common.constants.MessageKeys;
import com.omnicore.identity.common.constants.PaginationDefaults;
import com.omnicore.identity.rbac.AuthorityNames;
import com.omnicore.identity.permission.dto.CreatePermissionRequest;
import com.omnicore.identity.permission.dto.PermissionCreateResult;
import com.omnicore.identity.permission.dto.PermissionDetailResponse;
import com.omnicore.identity.permission.dto.PermissionListQuery;
import com.omnicore.identity.permission.dto.PermissionListResponse;
import com.omnicore.identity.permission.dto.ReplacePermissionDependenciesRequest;
import com.omnicore.identity.permission.dto.UpdatePermissionRequest;
import com.omnicore.identity.permission.error.PermissionBusinessException;
import com.omnicore.identity.permission.error.PermissionErrorCode;
import com.omnicore.identity.permission.error.PermissionNotFoundException;
import com.omnicore.identity.role.Role;
import com.omnicore.identity.role.RolePermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

  private final PermissionRepository permissionRepository;
  private final PermissionDependencyRepository permissionDependencyRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final PermissionMapper permissionMapper;
  private final PermissionValidator permissionValidator;
  private final PermissionDependencyValidator permissionDependencyValidator;
  private final MessageResolver messageResolver;
  private final SecurityUtils securityUtils;

  @Transactional(readOnly = true)
  public PageResponse<PermissionListResponse> listPermissions(PermissionListQuery query) {
    if (Boolean.FALSE.equals(query.isActive())) {
      requireViewDeletedPermission();
    }
    String keyword = normalizeKeyword(query.keyword());
    String module = normalizeFilterToken(query.module(), permissionValidator::normalizeModule);
    String action = normalizeFilterToken(query.action(), permissionValidator::normalizeAction);

    int page = resolvePage(query.page());
    int size = resolveSize(query.size());
    String sortBy = resolveSortBy(query.sortBy());
    Sort.Direction sortDirection = resolveSortDirection(query.sortDirection());

    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    Specification<Permission> specification =
        PermissionSpecification.withFilters(
            keyword, module, action, query.isActive(), query.isSystem());

    Page<Permission> permissionPage = permissionRepository.findAll(specification, pageable);
    Page<PermissionListResponse> responsePage =
        permissionPage.map(
            permission ->
                permissionMapper.toListResponse(
                    permission,
                    permissionDependencyRepository.countByPermissionId(permission.getId()),
                    rolePermissionRepository.countByPermissionId(permission.getId())));

    return PageResponse.from(responsePage);
  }

  @Transactional(readOnly = true)
  public PermissionDetailResponse getPermissionDetail(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Permission ID must not be null");
    }

    Permission permission =
        permissionRepository.findById(id).orElseThrow(PermissionNotFoundException::new);

    if (requiresViewDeletedPermission(permission)) {
      requireViewDeletedPermission();
    }

    return buildDetailResponse(permission);
  }

  @Transactional
  public PermissionCreateResult createPermission(
      CreatePermissionRequest request, Long currentUserId) {
    String normalizedName = permissionValidator.normalizeName(request.name());
    String normalizedModule = permissionValidator.normalizeModule(request.module());
    String normalizedAction = permissionValidator.normalizeAction(request.action());
    String normalizedDescription = normalizeDescription(request.description());

    permissionValidator.validatePermissionNameFormat(
        request.name(), request.action(), request.module());
    permissionValidator.validateDescriptionLength(normalizedDescription);

    Optional<Permission> existingPermission = permissionRepository.findByName(normalizedName);
    if (existingPermission.isPresent()) {
      Permission permission = existingPermission.get();
      if (permission.isActive() && !permission.isDeleted()) {
        throw new PermissionBusinessException(PermissionErrorCode.PERMISSION_ALREADY_EXISTS);
      }

      Permission reactivated =
          reactivatePermission(
              permission, normalizedModule, normalizedAction, normalizedDescription, currentUserId);
      return saveWithDependencies(reactivated, request.dependencyIds(), true);
    }

    Permission created =
        createNewPermission(
            normalizedName,
            normalizedModule,
            normalizedAction,
            normalizedDescription,
            currentUserId);
    return saveWithDependencies(created, request.dependencyIds(), false);
  }

  @Transactional
  public PermissionDetailResponse updatePermission(
      Long id, UpdatePermissionRequest request, Long currentUserId) {
    Permission permission =
        permissionRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(PermissionNotFoundException::new);

    if (!permission.isActive()) {
      throw new PermissionBusinessException(PermissionErrorCode.PERMISSION_INACTIVE);
    }

    if (permission.isSystem()) {
      return updateSystemPermission(permission, request, currentUserId);
    }

    return updateCustomPermission(permission, request, currentUserId);
  }

  @Transactional
  public void deletePermission(Long id, Long currentUserId) {
    Permission permission =
        permissionRepository.findById(id).orElseThrow(PermissionNotFoundException::new);

    if (!permission.isActive()) {
      throw new PermissionBusinessException(PermissionErrorCode.PERMISSION_INACTIVE);
    }

    if (permission.isSystem()) {
      throw new PermissionBusinessException(
          PermissionErrorCode.SYSTEM_PERMISSION_DELETE_NOT_ALLOWED);
    }

    if (rolePermissionRepository.countActiveRolesByPermissionId(id) > 0) {
      throw new PermissionBusinessException(PermissionErrorCode.PERMISSION_ASSIGNED_TO_ROLE);
    }

    if (permissionDependencyRepository.countActiveParentsByDependencyPermissionId(id) > 0) {
      throw new PermissionBusinessException(PermissionErrorCode.PERMISSION_USED_AS_DEPENDENCY);
    }

    // TODO: Block delete when active menus use this permission once Menu module exists.

    permissionDependencyRepository.deleteByPermissionId(id);

    Instant now = Instant.now();
    permission.setActive(false);
    permission.setDeletedAt(now);
    permission.setDeletedBy(currentUserId);
    permission.setUpdatedBy(currentUserId);
    permissionRepository.save(permission);
  }

  @Transactional
  public PermissionDetailResponse activatePermission(Long id, Long currentUserId) {
    Permission permission =
        permissionRepository.findById(id).orElseThrow(PermissionNotFoundException::new);

    if (permission.isActive()) {
      throw new PermissionBusinessException(PermissionErrorCode.PERMISSION_ALREADY_ACTIVE);
    }

    permission.setActive(true);
    permission.setDeletedAt(null);
    permission.setDeletedBy(null);
    permission.setUpdatedBy(currentUserId);

    return buildDetailResponse(permissionRepository.save(permission));
  }

  @Transactional
  public PermissionDetailResponse replaceDependencies(
      Long id, ReplacePermissionDependenciesRequest request, Long currentUserId) {
    Permission permission =
        permissionRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(PermissionNotFoundException::new);

    if (!permission.isActive()) {
      throw new PermissionBusinessException(PermissionErrorCode.PERMISSION_INACTIVE);
    }

    permission.setUpdatedBy(currentUserId);
    return buildDetailResponse(
        replaceDependenciesOnPermission(permission, request.dependencyIds()));
  }

  @Transactional
  public void removeDependency(Long id, Long dependencyId, Long currentUserId) {
    Permission permission =
        permissionRepository.findById(id).orElseThrow(PermissionNotFoundException::new);

    permissionRepository
        .findById(dependencyId)
        .orElseThrow(
            () ->
                new PermissionBusinessException(
                    PermissionErrorCode.DEPENDENCY_PERMISSION_NOT_FOUND));

    if (permissionDependencyRepository.countByPermissionIdAndDependencyPermissionId(
            id, dependencyId)
        > 0) {
      permissionDependencyRepository.deleteByPermissionIdAndDependencyPermissionId(
          id, dependencyId);
      permission.setUpdatedBy(currentUserId);
      permissionRepository.save(permission);
    }
  }

  private PermissionCreateResult saveWithDependencies(
      Permission permission, List<Long> dependencyIds, boolean reactivated) {
    Permission savedPermission = replaceDependenciesOnPermission(permission, dependencyIds);
    return new PermissionCreateResult(buildDetailResponse(savedPermission), reactivated);
  }

  private Permission replaceDependenciesOnPermission(
      Permission permission, List<Long> dependencyIds) {
    List<Permission> dependencies =
        permissionDependencyValidator.resolveAndValidateDependencies(
            permission.getId(), dependencyIds);

    permissionDependencyRepository.deleteByPermissionId(permission.getId());
    permission.getDependencies().clear();
    permission.getDependencies().addAll(dependencies);
    return permissionRepository.save(permission);
  }

  private PermissionDetailResponse updateSystemPermission(
      Permission permission, UpdatePermissionRequest request, Long currentUserId) {
    validateSystemPermissionUpdate(permission, request);

    if (request.description() != null) {
      String normalizedDescription = normalizeDescription(request.description());
      permissionValidator.validateDescriptionLength(normalizedDescription);
      permission.setDescription(normalizedDescription);
    }

    permission.setUpdatedBy(currentUserId);
    return buildDetailResponse(permissionRepository.save(permission));
  }

  private PermissionDetailResponse updateCustomPermission(
      Permission permission, UpdatePermissionRequest request, Long currentUserId) {
    validateCustomPermissionUpdateRequest(request);

    String normalizedName = permissionValidator.normalizeName(request.name());
    String normalizedModule = permissionValidator.normalizeModule(request.module());
    String normalizedAction = permissionValidator.normalizeAction(request.action());
    String normalizedDescription = normalizeDescription(request.description());

    permissionValidator.validatePermissionNameFormat(
        request.name(), request.action(), request.module());
    permissionValidator.validateDescriptionLength(normalizedDescription);

    if (permissionRepository.existsByNameAndIdNot(normalizedName, permission.getId())) {
      throw new PermissionBusinessException(PermissionErrorCode.PERMISSION_ALREADY_EXISTS);
    }

    permission.setName(normalizedName);
    permission.setModule(normalizedModule);
    permission.setAction(normalizedAction);
    permission.setDescription(normalizedDescription);
    permission.setUpdatedBy(currentUserId);
    permissionRepository.save(permission);

    if (request.dependencyIds() != null) {
      permission = replaceDependenciesOnPermission(permission, request.dependencyIds());
    }

    return buildDetailResponse(permission);
  }

  private void validateCustomPermissionUpdateRequest(UpdatePermissionRequest request) {
    if (!StringUtils.hasText(request.name())) {
      throw new IllegalArgumentException("Permission name must not be blank");
    }
    if (!StringUtils.hasText(request.module())) {
      throw new IllegalArgumentException("Permission module must not be blank");
    }
    if (!StringUtils.hasText(request.action())) {
      throw new IllegalArgumentException("Permission action must not be blank");
    }
  }

  private void validateSystemPermissionUpdate(
      Permission permission, UpdatePermissionRequest request) {
    if (request.dependencyIds() != null) {
      throw new PermissionBusinessException(
          PermissionErrorCode.SYSTEM_PERMISSION_UPDATE_RESTRICTED);
    }

    if (request.name() != null
        && !Objects.equals(
            permissionValidator.normalizeName(request.name()), permission.getName())) {
      throw new PermissionBusinessException(
          PermissionErrorCode.SYSTEM_PERMISSION_UPDATE_RESTRICTED);
    }

    if (request.module() != null
        && !Objects.equals(
            permissionValidator.normalizeModule(request.module()), permission.getModule())) {
      throw new PermissionBusinessException(
          PermissionErrorCode.SYSTEM_PERMISSION_UPDATE_RESTRICTED);
    }

    if (request.action() != null
        && !Objects.equals(
            permissionValidator.normalizeAction(request.action()), permission.getAction())) {
      throw new PermissionBusinessException(
          PermissionErrorCode.SYSTEM_PERMISSION_UPDATE_RESTRICTED);
    }
  }

  private Permission createNewPermission(
      String name, String module, String action, String description, Long currentUserId) {
    Permission permission =
        Permission.builder()
            .name(name)
            .module(module)
            .action(action)
            .description(description)
            .active(true)
            .system(false)
            .createdBy(currentUserId)
            .updatedBy(currentUserId)
            .build();

    return permissionRepository.save(permission);
  }

  private Permission reactivatePermission(
      Permission permission, String module, String action, String description, Long currentUserId) {
    permission.setActive(true);
    permission.setDeletedAt(null);
    permission.setDeletedBy(null);
    permission.setModule(module);
    permission.setAction(action);
    permission.setDescription(description);
    permission.setUpdatedBy(currentUserId);
    return permissionRepository.save(permission);
  }

  private void requireViewDeletedPermission() {
    if (!securityUtils.hasAuthority(AuthorityNames.VIEW_DELETED_PERMISSION)) {
      throw new PermissionBusinessException(PermissionErrorCode.VIEW_DELETED_PERMISSION_REQUIRED);
    }
  }

  private boolean requiresViewDeletedPermission(Permission permission) {
    return !permission.isActive() || permission.isDeleted();
  }

  private PermissionDetailResponse buildDetailResponse(Permission permission) {
    List<Permission> dependencies =
        permissionDependencyRepository.findActiveDependenciesByPermissionId(permission.getId());
    List<Permission> usedAsDependencyBy =
        permissionDependencyRepository.findActivePermissionsDependingOn(permission.getId());
    List<Role> usedByRoles =
        rolePermissionRepository.findActiveRolesByPermissionId(permission.getId());

    return permissionMapper.toDetailResponse(
        permission,
        dependencies.stream().map(permissionMapper::toSummary).toList(),
        usedAsDependencyBy.stream().map(permissionMapper::toSummary).toList(),
        usedByRoles.stream().map(permissionMapper::toRoleSummary).toList());
  }

  private String normalizeDescription(String description) {
    if (description == null) {
      return null;
    }
    String trimmed = description.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private String normalizeKeyword(String keyword) {
    if (!StringUtils.hasText(keyword)) {
      return null;
    }
    return keyword.trim();
  }

  private String normalizeFilterToken(
      String value, java.util.function.Function<String, String> normalizer) {
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
