package com.omnicore.identity.role;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.omnicore.identity.common.PageResponse;
import com.omnicore.identity.common.constants.PaginationDefaults;
import com.omnicore.identity.permission.Permission;
import com.omnicore.identity.permission.PermissionRepository;
import com.omnicore.identity.permission.dto.PermissionSummaryResponse;
import com.omnicore.identity.role.dto.CreateRoleRequest;
import com.omnicore.identity.role.dto.ReplaceRolePermissionsRequest;
import com.omnicore.identity.role.dto.ReplaceUserRolesRequest;
import com.omnicore.identity.role.dto.RoleCreateResult;
import com.omnicore.identity.role.dto.RoleDetailResponse;
import com.omnicore.identity.role.dto.RoleListQuery;
import com.omnicore.identity.role.dto.RoleListResponse;
import com.omnicore.identity.role.dto.UpdateRoleRequest;
import com.omnicore.identity.role.dto.UserRoleAssignmentResponse;
import com.omnicore.identity.role.error.RoleBusinessException;
import com.omnicore.identity.role.error.RoleErrorCode;
import com.omnicore.identity.role.error.RoleNotFoundException;
import com.omnicore.identity.user.User;
import com.omnicore.identity.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final UserRepository userRepository;
  private final RoleMapper roleMapper;
  private final RoleValidator roleValidator;

  @Override
  @Transactional(readOnly = true)
  public PageResponse<RoleListResponse> listRoles(RoleListQuery query) {
    int page = Math.max(query.page(), PaginationDefaults.DEFAULT_PAGE_NUMBER);
    int size = resolveSize(query.size());
    String sortBy = resolveSortBy(query.sortBy());
    Sort.Direction sortDirection = resolveSortDirection(query.sortDirection());

    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    Specification<Role> specification =
        RoleSpecification.withFilters(
            normalizeKeyword(query.keyword()), query.isActive(), query.isSystem());

    Page<RoleListResponse> responsePage =
        roleRepository
            .findAll(specification, pageable)
            .map(
                role ->
                    roleMapper.toListResponse(
                        role,
                        role.getPermissions().stream()
                            .filter(permission -> permission.isActive() && !permission.isDeleted())
                            .count(),
                        roleRepository.countAssignedUsersByRoleId(role.getId())));

    return PageResponse.from(responsePage);
  }

  @Override
  @Transactional(readOnly = true)
  public RoleDetailResponse getRoleDetail(Long id) {
    Role role =
        roleRepository
            .findWithPermissionsByIdAndDeletedAtIsNull(id)
            .orElseThrow(RoleNotFoundException::new);

    return buildDetailResponse(role);
  }

  @Override
  @Transactional
  public RoleCreateResult createRole(CreateRoleRequest request, Long currentUserId) {
    String name = roleValidator.normalizeName(request.name());
    String description = roleValidator.normalizeDescription(request.description());
    roleValidator.validateUniqueIds(
        request.permissionIds(), RoleErrorCode.DUPLICATE_ROLE_PERMISSION);

    Optional<Role> existingRole = roleRepository.findByName(name);
    if (existingRole.isPresent()) {
      Role role = existingRole.get();
      if (role.isActive() && !role.isDeleted()) {
        throw new RoleBusinessException(RoleErrorCode.ROLE_ALREADY_EXISTS);
      }

      role.setDescription(description);
      role.setActive(true);
      role.setDeletedAt(null);
      role.setDeletedBy(null);
      role.setUpdatedBy(currentUserId);
      role = roleRepository.save(role);
      role = replacePermissions(role, request.permissionIds());
      return new RoleCreateResult(buildDetailResponse(role), true);
    }

    Role role =
        Role.builder()
            .name(name)
            .description(description)
            .active(true)
            .system(false)
            .createdBy(currentUserId)
            .updatedBy(currentUserId)
            .build();

    role = roleRepository.save(role);
    role = replacePermissions(role, request.permissionIds());
    return new RoleCreateResult(buildDetailResponse(role), false);
  }

  @Override
  @Transactional
  public RoleDetailResponse updateRole(Long id, UpdateRoleRequest request, Long currentUserId) {
    Role role =
        roleRepository
            .findWithPermissionsByIdAndDeletedAtIsNull(id)
            .orElseThrow(RoleNotFoundException::new);

    if (!role.isActive()) {
      throw new RoleBusinessException(RoleErrorCode.ROLE_INACTIVE);
    }

    if (role.isSystem() && (request.name() != null || request.permissionIds() != null)) {
      throw new RoleBusinessException(RoleErrorCode.SYSTEM_ROLE_UPDATE_RESTRICTED);
    }

    if (request.name() != null) {
      String name = roleValidator.normalizeName(request.name());
      if (roleRepository.existsByNameAndIdNot(name, role.getId())) {
        throw new RoleBusinessException(RoleErrorCode.ROLE_ALREADY_EXISTS);
      }
      role.setName(name);
    }

    if (request.description() != null) {
      role.setDescription(roleValidator.normalizeDescription(request.description()));
    }

    role.setUpdatedBy(currentUserId);
    role = roleRepository.save(role);

    if (request.permissionIds() != null) {
      roleValidator.validateUniqueIds(
          request.permissionIds(), RoleErrorCode.DUPLICATE_ROLE_PERMISSION);
      role = replacePermissions(role, request.permissionIds());
    }

    return buildDetailResponse(role);
  }

  @Override
  @Transactional
  public void deleteRole(Long id, Long currentUserId) {
    Role role =
        roleRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(RoleNotFoundException::new);

    if (role.isSystem()) {
      throw new RoleBusinessException(RoleErrorCode.SYSTEM_ROLE_DELETE_RESTRICTED);
    }

    if (roleRepository.countAssignedUsersByRoleId(role.getId()) > 0) {
      throw new RoleBusinessException(RoleErrorCode.ROLE_ASSIGNED_TO_USERS);
    }

    role.setActive(false);
    role.setDeletedAt(Instant.now());
    role.setDeletedBy(currentUserId);
    role.setUpdatedBy(currentUserId);
    roleRepository.save(role);
  }

  @Override
  @Transactional
  public RoleDetailResponse replaceRolePermissions(
      Long roleId, ReplaceRolePermissionsRequest request, Long currentUserId) {
    Role role =
        roleRepository
            .findWithPermissionsByIdAndDeletedAtIsNull(roleId)
            .orElseThrow(RoleNotFoundException::new);

    if (!role.isActive()) {
      throw new RoleBusinessException(RoleErrorCode.ROLE_INACTIVE);
    }
    if (role.isSystem()) {
      throw new RoleBusinessException(RoleErrorCode.SYSTEM_ROLE_UPDATE_RESTRICTED);
    }

    roleValidator.validateUniqueIds(
        request.permissionIds(), RoleErrorCode.DUPLICATE_ROLE_PERMISSION);
    role.setUpdatedBy(currentUserId);
    return buildDetailResponse(replacePermissions(role, request.permissionIds()));
  }

  @Override
  @Transactional
  public UserRoleAssignmentResponse assignRoleToUser(
      Long roleId, Long userId, Long currentUserId) {
    Role role = resolveActiveRole(roleId);
    User user = resolveActiveUserWithRoles(userId);

    boolean alreadyAssigned =
        user.getRoles().stream()
            .anyMatch(existing -> Objects.equals(existing.getId(), role.getId()));
    if (!alreadyAssigned) {
      user.getRoles().add(role);
      touchUserRoleAssignment(user, currentUserId);
    }

    return roleMapper.toUserRoleAssignmentResponse(user);
  }

  @Override
  @Transactional
  public UserRoleAssignmentResponse removeRoleFromUser(
      Long roleId, Long userId, Long currentUserId) {
    User user = resolveActiveUserWithRoles(userId);
    boolean removed = user.getRoles().removeIf(role -> Objects.equals(role.getId(), roleId));
    if (removed) {
      touchUserRoleAssignment(user, currentUserId);
    }
    return roleMapper.toUserRoleAssignmentResponse(user);
  }

  @Override
  @Transactional
  public UserRoleAssignmentResponse replaceUserRoles(
      Long userId, ReplaceUserRolesRequest request, Long currentUserId) {
    roleValidator.validateUniqueIds(request.roleIds(), RoleErrorCode.DUPLICATE_USER_ROLE);
    User user = resolveActiveUserWithRoles(userId);
    Set<Role> roles = new LinkedHashSet<>(resolveActiveRoles(request.roleIds()));
    user.getRoles().clear();
    user.getRoles().addAll(roles);
    touchUserRoleAssignment(user, currentUserId);
    return roleMapper.toUserRoleAssignmentResponse(user);
  }

  private Role replacePermissions(Role role, List<Long> permissionIds) {
    role.getPermissions().clear();
    role.getPermissions().addAll(resolveActivePermissions(permissionIds));
    return roleRepository.save(role);
  }

  private List<Permission> resolveActivePermissions(List<Long> permissionIds) {
    if (permissionIds == null || permissionIds.isEmpty()) {
      return List.of();
    }

    List<Permission> permissions =
        permissionRepository.findAllByIdInAndActiveTrueAndDeletedAtIsNull(permissionIds);
    if (permissions.size() != new LinkedHashSet<>(permissionIds).size()) {
      throw new RoleBusinessException(RoleErrorCode.ROLE_PERMISSION_NOT_FOUND);
    }
    return permissions;
  }

  private List<Role> resolveActiveRoles(List<Long> roleIds) {
    if (roleIds == null || roleIds.isEmpty()) {
      return List.of();
    }

    List<Role> roles = roleRepository.findAllByIdInAndActiveTrueAndDeletedAtIsNull(roleIds);
    if (roles.size() != new LinkedHashSet<>(roleIds).size()) {
      throw new RoleNotFoundException();
    }
    return roles;
  }

  private Role resolveActiveRole(Long roleId) {
    return roleRepository
        .findByIdAndActiveTrueAndDeletedAtIsNull(roleId)
        .orElseThrow(RoleNotFoundException::new);
  }

  private User resolveActiveUserWithRoles(Long userId) {
    User user =
        userRepository
            .findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new RoleBusinessException(RoleErrorCode.USER_NOT_FOUND));
    if (!user.isActive()) {
      throw new RoleBusinessException(RoleErrorCode.USER_INACTIVE);
    }
    return user;
  }

  private void touchUserRoleAssignment(User user, Long currentUserId) {
    user.setTokenVersion(user.getTokenVersion() + 1);
    user.setUpdatedBy(currentUserId);
    userRepository.save(user);
  }

  private RoleDetailResponse buildDetailResponse(Role role) {
    List<PermissionSummaryResponse> permissions =
        role.getPermissions().stream()
            .filter(permission -> permission.isActive() && !permission.isDeleted())
            .map(roleMapper::toPermissionSummary)
            .sorted(java.util.Comparator.comparing(PermissionSummaryResponse::name))
            .toList();

    return roleMapper.toDetailResponse(
        role, permissions, roleRepository.countAssignedUsersByRoleId(role.getId()));
  }

  private int resolveSize(int size) {
    if (size < 1) {
      return PaginationDefaults.DEFAULT_PAGE_SIZE;
    }
    return Math.min(size, PaginationDefaults.MAX_PAGE_SIZE);
  }

  private String resolveSortBy(String sortBy) {
    if (!StringUtils.hasText(sortBy) || !RoleSearchConstants.ALLOWED_SORT_FIELDS.contains(sortBy)) {
      return RoleSearchConstants.DEFAULT_SORT_BY;
    }
    return sortBy;
  }

  private Sort.Direction resolveSortDirection(String sortDirection) {
    if (!StringUtils.hasText(sortDirection)) {
      return RoleSearchConstants.DEFAULT_SORT_DIRECTION;
    }
    try {
      return Sort.Direction.fromString(sortDirection);
    } catch (IllegalArgumentException ex) {
      return RoleSearchConstants.DEFAULT_SORT_DIRECTION;
    }
  }

  private String normalizeKeyword(String keyword) {
    if (!StringUtils.hasText(keyword)) {
      return null;
    }
    return keyword.trim();
  }
}
