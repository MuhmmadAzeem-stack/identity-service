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
import com.omnicore.identity.permission.dto.ReplacePermissionDependenciesRequest;
import com.omnicore.identity.permission.error.PermissionBusinessException;
import com.omnicore.identity.permission.error.PermissionErrorCode;
import com.omnicore.identity.permission.error.PermissionNotFoundException;
import com.omnicore.identity.role.RolePermissionRepository;

@ExtendWith(MockitoExtension.class)
class PermissionServiceDependencyTest {

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
  void replaceDependenciesShouldReplaceDependenciesForActivePermission() {
    Permission permission =
        Permission.builder().id(9L).name("CREATE_USER").active(true).system(true).build();
    Permission dependency = Permission.builder().id(14L).name("LIST_ROLE").build();
    ReplacePermissionDependenciesRequest request = new ReplacePermissionDependenciesRequest(List.of(14L));

    when(permissionRepository.findByIdAndDeletedAtIsNull(9L)).thenReturn(Optional.of(permission));
    when(permissionDependencyValidator.resolveAndValidateDependencies(9L, List.of(14L)))
        .thenReturn(List.of(dependency));
    when(permissionRepository.save(permission)).thenReturn(permission);
    stubDetailResponse(permission);

    permissionService.replaceDependencies(9L, request, 1L);

    assertEquals(1L, permission.getUpdatedBy());
    verify(permissionDependencyRepository).deleteByPermissionId(9L);
  }

  @Test
  void replaceDependenciesShouldClearDependenciesWhenEmptyListProvided() {
    Permission permission =
        Permission.builder().id(50L).name("EXPORT_USER").active(true).system(false).build();
    ReplacePermissionDependenciesRequest request = new ReplacePermissionDependenciesRequest(List.of());

    when(permissionRepository.findByIdAndDeletedAtIsNull(50L)).thenReturn(Optional.of(permission));
    when(permissionDependencyValidator.resolveAndValidateDependencies(50L, List.of()))
        .thenReturn(List.of());
    when(permissionRepository.save(permission)).thenReturn(permission);
    stubDetailResponse(permission);

    permissionService.replaceDependencies(50L, request, 1L);

    verify(permissionDependencyRepository).deleteByPermissionId(50L);
  }

  @Test
  void replaceDependenciesShouldRejectInactivePermission() {
    Permission permission =
        Permission.builder().id(50L).name("EXPORT_USER").active(false).system(false).build();

    when(permissionRepository.findByIdAndDeletedAtIsNull(50L)).thenReturn(Optional.of(permission));

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class,
            () ->
                permissionService.replaceDependencies(
                    50L, new ReplacePermissionDependenciesRequest(List.of()), 1L));

    assertEquals(PermissionErrorCode.PERMISSION_INACTIVE, exception.getErrorCode());
  }

  @Test
  void replaceDependenciesShouldThrowWhenPermissionNotFound() {
    when(permissionRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

    assertThrows(
        PermissionNotFoundException.class,
        () ->
            permissionService.replaceDependencies(
                999L, new ReplacePermissionDependenciesRequest(List.of()), 1L));
  }

  @Test
  void removeDependencyShouldDeleteMappingWhenExists() {
    Permission permission = Permission.builder().id(9L).name("CREATE_USER").build();
    Permission dependency = Permission.builder().id(14L).name("LIST_ROLE").build();

    when(permissionRepository.findById(9L)).thenReturn(Optional.of(permission));
    when(permissionRepository.findById(14L)).thenReturn(Optional.of(dependency));
    when(permissionDependencyRepository.countByPermissionIdAndDependencyPermissionId(9L, 14L))
        .thenReturn(1L);
    when(permissionRepository.save(permission)).thenReturn(permission);

    permissionService.removeDependency(9L, 14L, 1L);

    verify(permissionDependencyRepository)
        .deleteByPermissionIdAndDependencyPermissionId(9L, 14L);
    assertEquals(1L, permission.getUpdatedBy());
  }

  @Test
  void removeDependencyShouldSucceedWhenMappingDoesNotExist() {
    Permission permission = Permission.builder().id(9L).name("CREATE_USER").build();
    Permission dependency = Permission.builder().id(14L).name("LIST_ROLE").build();

    when(permissionRepository.findById(9L)).thenReturn(Optional.of(permission));
    when(permissionRepository.findById(14L)).thenReturn(Optional.of(dependency));
    when(permissionDependencyRepository.countByPermissionIdAndDependencyPermissionId(9L, 14L))
        .thenReturn(0L);

    permissionService.removeDependency(9L, 14L, 1L);

    verify(permissionDependencyRepository, never())
        .deleteByPermissionIdAndDependencyPermissionId(9L, 14L);
    verify(permissionRepository, never()).save(permission);
  }

  @Test
  void removeDependencyShouldThrowWhenMainPermissionNotFound() {
    when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(
        PermissionNotFoundException.class,
        () -> permissionService.removeDependency(999L, 14L, 1L));
  }

  @Test
  void removeDependencyShouldThrowWhenDependencyPermissionNotFound() {
    Permission permission = Permission.builder().id(9L).name("CREATE_USER").build();

    when(permissionRepository.findById(9L)).thenReturn(Optional.of(permission));
    when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class,
            () -> permissionService.removeDependency(9L, 999L, 1L));

    assertEquals(PermissionErrorCode.DEPENDENCY_PERMISSION_NOT_FOUND, exception.getErrorCode());
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
