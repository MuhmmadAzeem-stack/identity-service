package com.omnicore.identity.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.omnicore.identity.permission.dto.PermissionDetailResponse;
import com.omnicore.identity.permission.error.PermissionBusinessException;
import com.omnicore.identity.permission.error.PermissionErrorCode;
import com.omnicore.identity.permission.error.PermissionNotFoundException;
import com.omnicore.identity.role.RolePermissionRepository;

@ExtendWith(MockitoExtension.class)
class PermissionServiceDeleteActivateTest {

  @Mock private PermissionRepository permissionRepository;

  @Mock private PermissionDependencyRepository permissionDependencyRepository;

  @Mock private RolePermissionRepository rolePermissionRepository;

  @Mock private PermissionMapper permissionMapper;

  @Mock private PermissionValidator permissionValidator;

  @Mock private PermissionDependencyValidator permissionDependencyValidator;

  @Mock private com.omnicore.identity.common.MessageResolver messageResolver;

  @Mock private com.omnicore.identity.common.SecurityUtils securityUtils;

  @InjectMocks private PermissionService permissionService;

  @Test
  void deletePermissionShouldSoftDeleteCustomUnusedPermission() {
    Permission permission =
        Permission.builder()
            .id(50L)
            .name("EXPORT_USER")
            .active(true)
            .system(false)
            .build();

    when(permissionRepository.findById(50L)).thenReturn(Optional.of(permission));
    when(rolePermissionRepository.countActiveRolesByPermissionId(50L)).thenReturn(0L);
    when(permissionDependencyRepository.countActiveParentsByDependencyPermissionId(50L))
        .thenReturn(0L);
    when(permissionRepository.save(permission)).thenReturn(permission);

    permissionService.deletePermission(50L, 1L);

    assertFalse(permission.isActive());
    assertEquals(1L, permission.getDeletedBy());
    assertEquals(1L, permission.getUpdatedBy());
    assertTrue(permission.getDeletedAt() != null);
    verify(permissionDependencyRepository).deleteByPermissionId(50L);
  }

  @Test
  void deletePermissionShouldRejectAlreadyInactivePermission() {
    Permission permission =
        Permission.builder()
            .id(50L)
            .name("EXPORT_USER")
            .active(false)
            .deletedAt(Instant.now())
            .system(false)
            .build();

    when(permissionRepository.findById(50L)).thenReturn(Optional.of(permission));

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class, () -> permissionService.deletePermission(50L, 1L));

    assertEquals(PermissionErrorCode.PERMISSION_INACTIVE, exception.getErrorCode());
    verify(permissionDependencyRepository, never()).deleteByPermissionId(50L);
  }

  @Test
  void deletePermissionShouldRejectSystemPermission() {
    Permission permission =
        Permission.builder().id(9L).name("CREATE_USER").active(true).system(true).build();

    when(permissionRepository.findById(9L)).thenReturn(Optional.of(permission));

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class, () -> permissionService.deletePermission(9L, 1L));

    assertEquals(
        PermissionErrorCode.SYSTEM_PERMISSION_DELETE_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void deletePermissionShouldRejectWhenAssignedToActiveRole() {
    Permission permission =
        Permission.builder().id(50L).name("EXPORT_USER").active(true).system(false).build();

    when(permissionRepository.findById(50L)).thenReturn(Optional.of(permission));
    when(rolePermissionRepository.countActiveRolesByPermissionId(50L)).thenReturn(1L);

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class, () -> permissionService.deletePermission(50L, 1L));

    assertEquals(PermissionErrorCode.PERMISSION_ASSIGNED_TO_ROLE, exception.getErrorCode());
  }

  @Test
  void deletePermissionShouldRejectWhenUsedAsDependency() {
    Permission permission =
        Permission.builder().id(50L).name("EXPORT_USER").active(true).system(false).build();

    when(permissionRepository.findById(50L)).thenReturn(Optional.of(permission));
    when(rolePermissionRepository.countActiveRolesByPermissionId(50L)).thenReturn(0L);
    when(permissionDependencyRepository.countActiveParentsByDependencyPermissionId(50L))
        .thenReturn(1L);

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class, () -> permissionService.deletePermission(50L, 1L));

    assertEquals(PermissionErrorCode.PERMISSION_USED_AS_DEPENDENCY, exception.getErrorCode());
  }

  @Test
  void deletePermissionShouldThrowWhenPermissionNotFound() {
    when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(
        PermissionNotFoundException.class, () -> permissionService.deletePermission(999L, 1L));
  }

  @Test
  void activatePermissionShouldReactivateInactivePermission() {
    Permission permission =
        Permission.builder()
            .id(50L)
            .name("EXPORT_USER")
            .active(false)
            .deletedAt(Instant.now())
            .deletedBy(2L)
            .system(false)
            .build();

    when(permissionRepository.findById(50L)).thenReturn(Optional.of(permission));
    when(permissionRepository.save(permission)).thenReturn(permission);
    stubDetailResponse(permission);

    permissionService.activatePermission(50L, 1L);

    assertTrue(permission.isActive());
    assertNull(permission.getDeletedAt());
    assertNull(permission.getDeletedBy());
    assertEquals(1L, permission.getUpdatedBy());
    verify(permissionDependencyRepository, never()).deleteByPermissionId(50L);
  }

  @Test
  void activatePermissionShouldRejectAlreadyActivePermission() {
    Permission permission =
        Permission.builder().id(50L).name("EXPORT_USER").active(true).system(false).build();

    when(permissionRepository.findById(50L)).thenReturn(Optional.of(permission));

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class,
            () -> permissionService.activatePermission(50L, 1L));

    assertEquals(PermissionErrorCode.PERMISSION_ALREADY_ACTIVE, exception.getErrorCode());
  }

  @Test
  void activatePermissionShouldThrowWhenPermissionNotFound() {
    when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(
        PermissionNotFoundException.class, () -> permissionService.activatePermission(999L, 1L));
  }

  private void stubDetailResponse(Permission permission) {
    when(permissionDependencyRepository.findActiveDependenciesByPermissionId(permission.getId()))
        .thenReturn(List.of());
    when(permissionDependencyRepository.findActivePermissionsDependingOn(permission.getId()))
        .thenReturn(List.of());
    when(rolePermissionRepository.findActiveRolesByPermissionId(permission.getId()))
        .thenReturn(List.of());
    when(permissionMapper.toDetailResponse(any(), any(), any(), any()))
        .thenReturn(
            new PermissionDetailResponse(
                permission.getId(),
                permission.getName(),
                permission.getModule(),
                permission.getAction(),
                permission.getDescription(),
                permission.isActive(),
                permission.isSystem(),
                null,
                null,
                null,
                permission.getUpdatedBy(),
                List.of(),
                List.of(),
                List.of()));
  }
}
