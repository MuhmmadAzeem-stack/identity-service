package com.omnicore.identity.seed;

import java.util.List;

import com.omnicore.identity.rbac.AuthorityNames;

public final class CorePermissionDependencySeeds {

  private CorePermissionDependencySeeds() {}

  public static List<PermissionDependencySeed> all() {
    return List.of(
        dep(AuthorityNames.CREATE_USER, AuthorityNames.LIST_ROLE),
        dep(AuthorityNames.UPDATE_USER, AuthorityNames.VIEW_USER),
        dep(AuthorityNames.UPDATE_USER, AuthorityNames.LIST_ROLE),
        dep(AuthorityNames.DELETE_USER, AuthorityNames.VIEW_USER),
        dep(AuthorityNames.ASSIGN_USER_ROLE, AuthorityNames.LIST_ROLE),
        dep(AuthorityNames.VIEW_USER_PERMISSION, AuthorityNames.VIEW_USER),
        dep(AuthorityNames.CREATE_ROLE, AuthorityNames.LIST_PERMISSION),
        dep(AuthorityNames.UPDATE_ROLE, AuthorityNames.VIEW_ROLE),
        dep(AuthorityNames.UPDATE_ROLE, AuthorityNames.LIST_PERMISSION),
        dep(AuthorityNames.DELETE_ROLE, AuthorityNames.VIEW_ROLE),
        dep(AuthorityNames.ASSIGN_ROLE_PERMISSION, AuthorityNames.LIST_PERMISSION),
        dep(AuthorityNames.CREATE_PERMISSION, AuthorityNames.LIST_PERMISSION),
        dep(AuthorityNames.UPDATE_PERMISSION, AuthorityNames.VIEW_PERMISSION),
        dep(AuthorityNames.DELETE_PERMISSION, AuthorityNames.VIEW_PERMISSION),
        dep(AuthorityNames.ASSIGN_PERMISSION_DEPENDENCY, AuthorityNames.LIST_PERMISSION),
        dep(AuthorityNames.REMOVE_PERMISSION_DEPENDENCY, AuthorityNames.LIST_PERMISSION),
        dep(AuthorityNames.CREATE_MENU, AuthorityNames.LIST_PERMISSION),
        dep(AuthorityNames.UPDATE_MENU, AuthorityNames.VIEW_MENU),
        dep(AuthorityNames.UPDATE_MENU, AuthorityNames.LIST_PERMISSION),
        dep(AuthorityNames.DELETE_MENU, AuthorityNames.VIEW_MENU),
        dep(AuthorityNames.REORDER_MENU, AuthorityNames.VIEW_MENU),
        dep(AuthorityNames.ACTIVATE_MENU, AuthorityNames.VIEW_MENU),
        dep(AuthorityNames.DEACTIVATE_MENU, AuthorityNames.VIEW_MENU),
        dep(AuthorityNames.VIEW_AUDIT_LOG, AuthorityNames.LIST_AUDIT_LOG));
  }

  private static PermissionDependencySeed dep(String permission, String dependsOn) {
    return new PermissionDependencySeed(permission, dependsOn);
  }
}
