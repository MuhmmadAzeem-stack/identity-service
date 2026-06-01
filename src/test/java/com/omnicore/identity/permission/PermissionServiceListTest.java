package com.omnicore.identity.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.omnicore.identity.common.SecurityUtils;
import com.omnicore.identity.permission.dto.PermissionListQuery;
import com.omnicore.identity.permission.error.PermissionBusinessException;
import com.omnicore.identity.permission.error.PermissionErrorCode;
import com.omnicore.identity.rbac.AuthorityNames;
import com.omnicore.identity.role.RolePermissionRepository;

@ExtendWith(MockitoExtension.class)
class PermissionServiceListTest {

  @Mock private PermissionRepository permissionRepository;

  @Mock private PermissionDependencyRepository permissionDependencyRepository;

  @Mock private RolePermissionRepository rolePermissionRepository;

  @Mock private PermissionMapper permissionMapper;

  @Mock private PermissionValidator permissionValidator;

  @Mock private PermissionDependencyValidator permissionDependencyValidator;

  @Mock private com.omnicore.identity.common.MessageResolver messageResolver;

  @Mock private SecurityUtils securityUtils;

  @InjectMocks private PermissionService permissionService;

  @Test
  void listPermissionsShouldRequireViewDeletedPermissionWhenFilteringInactive() {
    PermissionListQuery query =
        new PermissionListQuery(null, null, null, false, null, 0, 20, "name", "ASC");

    when(securityUtils.hasAuthority(AuthorityNames.VIEW_DELETED_PERMISSION)).thenReturn(false);

    PermissionBusinessException exception =
        assertThrows(
            PermissionBusinessException.class, () -> permissionService.listPermissions(query));

    assertEquals(PermissionErrorCode.VIEW_DELETED_PERMISSION_REQUIRED, exception.getErrorCode());
    verify(permissionRepository, never()).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  void listPermissionsShouldAllowInactiveFilterWhenAuthorized() {
    PermissionListQuery query =
        new PermissionListQuery(null, null, null, false, null, 0, 20, "name", "ASC");

    when(securityUtils.hasAuthority(AuthorityNames.VIEW_DELETED_PERMISSION)).thenReturn(true);
    when(permissionRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(java.util.List.of()));

    permissionService.listPermissions(query);

    verify(permissionRepository).findAll(any(Specification.class), any(Pageable.class));
  }
}
