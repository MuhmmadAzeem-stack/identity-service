package com.omnicore.identity.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.omnicore.identity.permission.dto.PermissionDetailResponse;
import com.omnicore.identity.permission.dto.UpdatePermissionRequest;
import com.omnicore.identity.permission.error.PermissionBusinessException;
import com.omnicore.identity.permission.error.PermissionErrorCode;
import com.omnicore.identity.permission.error.PermissionNotFoundException;
import com.omnicore.identity.role.RolePermissionRepository;

@ExtendWith(MockitoExtension.class)
class PermissionServiceUpdateTest {

  @Mock private PermissionRepository permissionRepository;

  @Mock private PermissionDependencyRepository permissionDependencyRepository;

  @Mock private RolePermissionRepository rolePermissionRepository;

  @Mock private PermissionMapper permissionMapper;

  @Mock private PermissionValidator permissionValidator;

  @Mock private PermissionDependencyValidator permissionDependencyValidator;

  @Mock private com.omnicore.identity.common.MessageResolver messageResolver;

  @InjectMocks private PermissionService permissionService;

  @Test
  void updatePermissionShouldUpdateCustomPermissionFields() {
    Permission permission =
        Permission.builder()
            .id(50L)
            .name("EXPORT_USER")
            .module("USER")
            .action("EXPORT")
            .active(true)
            .system(false)
            .build();
    UpdatePermissionRequest request =
        new UpdatePermissionRequest(
            "EXPORT_USER", "USER", "EXPORT", "Updated export user permission", null);

    when(permissionRepository.findByIdAndDeletedAtIsNull(50L)).thenReturn(Optional.of(permission));
    when(permissionValidator.normalizeName("EXPORT_USER")).thenReturn("EXPORT_USER");
    when(permissionValidator.normalizeModule("USER")).thenReturn("USER");
    when(permissionValidator.normalizeAction("EXPORT")).thenReturn("EXPORT");
    when(permissionRepository.existsByNameAndIdNot("EXPORT_USER", 50L)).thenReturn(false);
    when(permissionRepository.save(permission)).thenReturn(permission);
    stubDetailResponse(permission);

    permissionService.updatePermission(50L, request, 1L);

    assertEquals("Updated export user permission", permission.getDescription());
    assertEquals(1L, permission.getUpdatedBy());
    verify(permissionDependencyRepository, never()).deleteByPermissionId(50L);
  }

  @Test
  void updatePermissionShouldRejectDuplicateName() {
    Permission permission =
        Permission.builder().id(50L).name("EXPORT_USER").active(true).system(false).build();
    UpdatePermissionRequest request =
        new UpdatePermissionRequest("CREATE_USER", "USER", "CREATE", "Desc", null);

    when(permissionRepository.findByIdAndDeletedAtIsNull(50L)).thenReturn(Optional.of(permission));
    when(permissionValidator.normalizeName("CREATE_USER")).thenReturn("CREATE_USER");
    when(permissionValidator.normalizeModule("USER")).thenReturn("USER");
    when(permissionValidator.normalizeAction("CREATE")).thenReturn("CREATE");
    when(permissionRepository.existsByNameAndIdNot("CREATE_USER", 50L)).thenReturn(true);

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class,
            () -> permissionService.updatePermission(50L, request, 1L));

    assertEquals(PermissionErrorCode.PERMISSION_ALREADY_EXISTS, exception.getErrorCode());
  }

  @Test
  void updatePermissionShouldAllowSystemPermissionDescriptionUpdate() {
    Permission permission =
        Permission.builder()
            .id(9L)
            .name("CREATE_USER")
            .module("USER")
            .action("CREATE")
            .active(true)
            .system(true)
            .build();
    UpdatePermissionRequest request =
        new UpdatePermissionRequest(null, null, null, "Updated create user description", null);

    when(permissionRepository.findByIdAndDeletedAtIsNull(9L)).thenReturn(Optional.of(permission));
    when(permissionRepository.save(permission)).thenReturn(permission);
    stubDetailResponse(permission);

    permissionService.updatePermission(9L, request, 1L);

    assertEquals("Updated create user description", permission.getDescription());
  }

  @Test
  void updatePermissionShouldRejectSystemPermissionNameChange() {
    Permission permission =
        Permission.builder().id(9L).name("CREATE_USER").active(true).system(true).build();
    UpdatePermissionRequest request =
        new UpdatePermissionRequest(
            "CREATE_APP_USER", "APP_USER", "CREATE", "Trying to rename system permission", null);

    when(permissionRepository.findByIdAndDeletedAtIsNull(9L)).thenReturn(Optional.of(permission));

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class,
            () -> permissionService.updatePermission(9L, request, 1L));

    assertEquals(PermissionErrorCode.SYSTEM_PERMISSION_UPDATE_RESTRICTED, exception.getErrorCode());
  }

  @Test
  void updatePermissionShouldRejectSystemPermissionDependencyUpdate() {
    Permission permission =
        Permission.builder().id(9L).name("CREATE_USER").active(true).system(true).build();
    UpdatePermissionRequest request =
        new UpdatePermissionRequest(null, null, null, "Updated description", List.of(10L));

    when(permissionRepository.findByIdAndDeletedAtIsNull(9L)).thenReturn(Optional.of(permission));

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class,
            () -> permissionService.updatePermission(9L, request, 1L));

    assertEquals(PermissionErrorCode.SYSTEM_PERMISSION_UPDATE_RESTRICTED, exception.getErrorCode());
  }

  @Test
  void updatePermissionShouldReplaceDependenciesWhenProvided() {
    Permission permission =
        Permission.builder().id(50L).name("EXPORT_USER").active(true).system(false).build();
    Permission dependency = Permission.builder().id(10L).name("LIST_USER").build();
    UpdatePermissionRequest request =
        new UpdatePermissionRequest("EXPORT_USER", "USER", "EXPORT", "Desc", List.of(10L));

    when(permissionRepository.findByIdAndDeletedAtIsNull(50L)).thenReturn(Optional.of(permission));
    when(permissionValidator.normalizeName("EXPORT_USER")).thenReturn("EXPORT_USER");
    when(permissionValidator.normalizeModule("USER")).thenReturn("USER");
    when(permissionValidator.normalizeAction("EXPORT")).thenReturn("EXPORT");
    when(permissionRepository.existsByNameAndIdNot("EXPORT_USER", 50L)).thenReturn(false);
    when(permissionDependencyValidator.resolveAndValidateDependencies(50L, List.of(10L)))
        .thenReturn(List.of(dependency));
    when(permissionRepository.save(permission)).thenReturn(permission);
    stubDetailResponse(permission);

    permissionService.updatePermission(50L, request, 1L);

    verify(permissionDependencyRepository).deleteByPermissionId(50L);
  }

  @Test
  void updatePermissionShouldClearDependenciesWhenEmptyListProvided() {
    Permission permission =
        Permission.builder().id(50L).name("EXPORT_USER").active(true).system(false).build();
    UpdatePermissionRequest request =
        new UpdatePermissionRequest("EXPORT_USER", "USER", "EXPORT", "Desc", List.of());

    when(permissionRepository.findByIdAndDeletedAtIsNull(50L)).thenReturn(Optional.of(permission));
    when(permissionValidator.normalizeName("EXPORT_USER")).thenReturn("EXPORT_USER");
    when(permissionValidator.normalizeModule("USER")).thenReturn("USER");
    when(permissionValidator.normalizeAction("EXPORT")).thenReturn("EXPORT");
    when(permissionRepository.existsByNameAndIdNot("EXPORT_USER", 50L)).thenReturn(false);
    when(permissionDependencyValidator.resolveAndValidateDependencies(50L, List.of()))
        .thenReturn(List.of());
    when(permissionRepository.save(permission)).thenReturn(permission);
    stubDetailResponse(permission);

    permissionService.updatePermission(50L, request, 1L);

    verify(permissionDependencyRepository).deleteByPermissionId(50L);
  }

  @Test
  void updatePermissionShouldRejectInactivePermission() {
    Permission permission =
        Permission.builder().id(50L).name("EXPORT_USER").active(false).system(false).build();

    when(permissionRepository.findByIdAndDeletedAtIsNull(50L)).thenReturn(Optional.of(permission));

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class,
            () ->
                permissionService.updatePermission(
                    50L,
                    new UpdatePermissionRequest("EXPORT_USER", "USER", "EXPORT", "Desc", null),
                    1L));

    assertEquals(PermissionErrorCode.PERMISSION_INACTIVE, exception.getErrorCode());
  }

  @Test
  void updatePermissionShouldThrowWhenPermissionNotFound() {
    when(permissionRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

    assertThrows(
        PermissionNotFoundException.class,
        () ->
            permissionService.updatePermission(
                999L,
                new UpdatePermissionRequest("EXPORT_USER", "USER", "EXPORT", null, null),
                1L));
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
