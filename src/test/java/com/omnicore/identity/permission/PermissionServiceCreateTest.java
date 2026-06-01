package com.omnicore.identity.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.omnicore.identity.permission.dto.CreatePermissionRequest;
import com.omnicore.identity.permission.dto.PermissionCreateResult;
import com.omnicore.identity.permission.dto.PermissionDetailResponse;
import com.omnicore.identity.permission.error.PermissionBusinessException;
import com.omnicore.identity.permission.error.PermissionErrorCode;
import com.omnicore.identity.role.RolePermissionRepository;

@ExtendWith(MockitoExtension.class)
class PermissionServiceCreateTest {

  @Mock private PermissionRepository permissionRepository;

  @Mock private PermissionDependencyRepository permissionDependencyRepository;

  @Mock private RolePermissionRepository rolePermissionRepository;

  @Mock private PermissionMapper permissionMapper;

  @Mock private PermissionValidator permissionValidator;

  @Mock private PermissionDependencyValidator permissionDependencyValidator;

  @Mock private com.omnicore.identity.common.MessageResolver messageResolver;

  @InjectMocks private PermissionService permissionService;

  @Test
  void createPermissionShouldCreateNewCustomPermission() {
    CreatePermissionRequest request =
        new CreatePermissionRequest(
            "EXPORT_USER", "USER", "EXPORT", "Allows exporting users", List.of());

    when(permissionValidator.normalizeName("EXPORT_USER")).thenReturn("EXPORT_USER");
    when(permissionValidator.normalizeModule("USER")).thenReturn("USER");
    when(permissionValidator.normalizeAction("EXPORT")).thenReturn("EXPORT");
    when(permissionRepository.findByName("EXPORT_USER")).thenReturn(Optional.empty());
    when(permissionRepository.save(any(Permission.class)))
        .thenAnswer(
            invocation -> {
              Permission permission = invocation.getArgument(0);
              permission.setId(50L);
              return permission;
            });
    when(permissionDependencyValidator.resolveAndValidateDependencies(50L, List.of()))
        .thenReturn(List.of());
    when(permissionDependencyRepository.findActiveDependenciesByPermissionId(50L))
        .thenReturn(List.of());
    when(permissionDependencyRepository.findActivePermissionsDependingOn(50L))
        .thenReturn(List.of());
    when(rolePermissionRepository.findActiveRolesByPermissionId(50L)).thenReturn(List.of());
    when(permissionMapper.toDetailResponse(any(), any(), any(), any()))
        .thenReturn(
            new PermissionDetailResponse(
                50L,
                "EXPORT_USER",
                "USER",
                "EXPORT",
                "Allows exporting users",
                true,
                false,
                null,
                1L,
                null,
                1L,
                List.of(),
                List.of(),
                List.of()));

    PermissionCreateResult result = permissionService.createPermission(request, 1L);

    assertFalse(result.reactivated());
    assertEquals("EXPORT_USER", result.permission().name());
    assertFalse(result.permission().system());
    verify(permissionDependencyRepository).deleteByPermissionId(50L);
  }

  @Test
  void createPermissionShouldRejectDuplicateActivePermission() {
    CreatePermissionRequest request =
        new CreatePermissionRequest(
            "EXPORT_USER", "USER", "EXPORT", "Allows exporting users", List.of());
    Permission existing = Permission.builder().id(10L).name("EXPORT_USER").active(true).build();

    when(permissionValidator.normalizeName("EXPORT_USER")).thenReturn("EXPORT_USER");
    when(permissionValidator.normalizeModule("USER")).thenReturn("USER");
    when(permissionValidator.normalizeAction("EXPORT")).thenReturn("EXPORT");
    when(permissionRepository.findByName("EXPORT_USER")).thenReturn(Optional.of(existing));

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class,
            () -> permissionService.createPermission(request, 1L));

    assertEquals(PermissionErrorCode.PERMISSION_ALREADY_EXISTS, exception.getErrorCode());
    verify(permissionRepository, never()).save(any());
  }

  @Test
  void createPermissionShouldReactivateInactivePermission() {
    CreatePermissionRequest request =
        new CreatePermissionRequest(
            "EXPORT_USER", "USER", "EXPORT", "Allows exporting users", List.of());
    Permission existing =
        Permission.builder().id(10L).name("EXPORT_USER").active(false).system(false).build();

    when(permissionValidator.normalizeName("EXPORT_USER")).thenReturn("EXPORT_USER");
    when(permissionValidator.normalizeModule("USER")).thenReturn("USER");
    when(permissionValidator.normalizeAction("EXPORT")).thenReturn("EXPORT");
    when(permissionRepository.findByName("EXPORT_USER")).thenReturn(Optional.of(existing));
    when(permissionRepository.save(existing)).thenReturn(existing);
    when(permissionDependencyValidator.resolveAndValidateDependencies(10L, List.of()))
        .thenReturn(List.of());
    when(permissionDependencyRepository.findActiveDependenciesByPermissionId(10L))
        .thenReturn(List.of());
    when(permissionDependencyRepository.findActivePermissionsDependingOn(10L))
        .thenReturn(List.of());
    when(rolePermissionRepository.findActiveRolesByPermissionId(10L)).thenReturn(List.of());
    when(permissionMapper.toDetailResponse(any(), any(), any(), any()))
        .thenReturn(
            new PermissionDetailResponse(
                10L,
                "EXPORT_USER",
                "USER",
                "EXPORT",
                "Allows exporting users",
                true,
                false,
                null,
                1L,
                null,
                1L,
                List.of(),
                List.of(),
                List.of()));

    PermissionCreateResult result = permissionService.createPermission(request, 1L);

    assertTrue(result.reactivated());
    assertTrue(existing.isActive());
    verify(permissionDependencyRepository).deleteByPermissionId(10L);
  }

  @Test
  void createPermissionShouldRejectInvalidNameFormat() {
    CreatePermissionRequest request =
        new CreatePermissionRequest("USER_EXPORT", "USER", "EXPORT", "Invalid format", List.of());

    when(permissionValidator.normalizeName("USER_EXPORT")).thenReturn("USER_EXPORT");
    when(permissionValidator.normalizeModule("USER")).thenReturn("USER");
    when(permissionValidator.normalizeAction("EXPORT")).thenReturn("EXPORT");
    org.mockito.Mockito.doThrow(
            new PermissionBusinessException(PermissionErrorCode.INVALID_PERMISSION_NAME_FORMAT))
        .when(permissionValidator)
        .validatePermissionNameFormat("USER_EXPORT", "EXPORT", "USER");

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class,
            () -> permissionService.createPermission(request, 1L));

    assertEquals(PermissionErrorCode.INVALID_PERMISSION_NAME_FORMAT, exception.getErrorCode());
  }
}
