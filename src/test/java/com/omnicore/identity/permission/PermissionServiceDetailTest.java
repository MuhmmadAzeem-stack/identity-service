package com.omnicore.identity.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.omnicore.identity.permission.dto.PermissionSummaryResponse;
import com.omnicore.identity.permission.error.PermissionNotFoundException;
import com.omnicore.identity.role.Role;
import com.omnicore.identity.role.RolePermissionRepository;
import com.omnicore.identity.role.dto.RoleSummaryResponse;

@ExtendWith(MockitoExtension.class)
class PermissionServiceDetailTest {

  @Mock private PermissionRepository permissionRepository;

  @Mock private PermissionDependencyRepository permissionDependencyRepository;

  @Mock private RolePermissionRepository rolePermissionRepository;

  @Mock private PermissionMapper permissionMapper;

  @Mock private PermissionValidator permissionValidator;

  @Mock private com.omnicore.identity.common.MessageResolver messageResolver;

  @InjectMocks private PermissionService permissionService;

  @Test
  void getPermissionDetailShouldFetchRelationsAndMapResponse() {
    Permission permission =
        Permission.builder().id(9L).name("CREATE_USER").module("USER").action("CREATE").build();
    Permission dependency = Permission.builder().id(14L).name("LIST_ROLE").build();
    Permission dependent = Permission.builder().id(9L).name("CREATE_USER").build();
    Role role = Role.builder().id(1L).name("SUPER_ADMIN").build();

    PermissionSummaryResponse dependencySummary =
        new PermissionSummaryResponse(14L, "LIST_ROLE", "ROLE", "LIST", "List roles", true, true);
    PermissionSummaryResponse dependentSummary =
        new PermissionSummaryResponse(
            9L, "CREATE_USER", "USER", "CREATE", "Create users", true, true);
    RoleSummaryResponse roleSummary =
        new RoleSummaryResponse(1L, "SUPER_ADMIN", "Full system access", true, true);
    PermissionDetailResponse detailResponse =
        new PermissionDetailResponse(
            9L,
            "CREATE_USER",
            "USER",
            "CREATE",
            null,
            true,
            true,
            null,
            null,
            null,
            null,
            List.of(dependencySummary),
            List.of(dependentSummary),
            List.of(roleSummary));

    when(permissionRepository.findByIdAndDeletedAtIsNull(9L)).thenReturn(Optional.of(permission));
    when(permissionDependencyRepository.findActiveDependenciesByPermissionId(9L))
        .thenReturn(List.of(dependency));
    when(permissionDependencyRepository.findActivePermissionsDependingOn(9L))
        .thenReturn(List.of(dependent));
    when(rolePermissionRepository.findActiveRolesByPermissionId(9L)).thenReturn(List.of(role));
    when(permissionMapper.toSummary(dependency)).thenReturn(dependencySummary);
    when(permissionMapper.toSummary(dependent)).thenReturn(dependentSummary);
    when(permissionMapper.toRoleSummary(role)).thenReturn(roleSummary);
    when(permissionMapper.toDetailResponse(
            permission,
            List.of(dependencySummary),
            List.of(dependentSummary),
            List.of(roleSummary)))
        .thenReturn(detailResponse);

    PermissionDetailResponse result = permissionService.getPermissionDetail(9L);

    assertSame(detailResponse, result);
    verify(permissionDependencyRepository).findActiveDependenciesByPermissionId(9L);
    verify(permissionDependencyRepository).findActivePermissionsDependingOn(9L);
    verify(rolePermissionRepository).findActiveRolesByPermissionId(9L);
  }

  @Test
  void getPermissionDetailShouldThrowWhenPermissionNotFound() {
    when(permissionRepository.findByIdAndDeletedAtIsNull(999999L)).thenReturn(Optional.empty());

    assertThrows(
        PermissionNotFoundException.class, () -> permissionService.getPermissionDetail(999999L));
  }

  @Test
  void getPermissionDetailShouldTreatSoftDeletedPermissionAsNotFound() {
    when(permissionRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.empty());

    assertThrows(
        PermissionNotFoundException.class, () -> permissionService.getPermissionDetail(5L));
  }

  @Test
  void getPermissionDetailShouldRejectNullId() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> permissionService.getPermissionDetail(null));

    assertEquals("Permission ID must not be null", exception.getMessage());
  }
}
