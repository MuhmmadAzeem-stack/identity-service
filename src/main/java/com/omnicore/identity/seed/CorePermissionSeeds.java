package com.omnicore.identity.seed;

import java.util.List;

import com.omnicore.identity.rbac.AuthorityNames;
import com.omnicore.identity.rbac.RbacAction;
import com.omnicore.identity.rbac.RbacModule;

public final class CorePermissionSeeds {

  private CorePermissionSeeds() {}

  public static List<CorePermissionSeed> all() {
    return List.of(
        user(RbacAction.LIST, "List users"),
        user(RbacAction.VIEW, "View user details"),
        user(RbacAction.CREATE, "Create users"),
        user(RbacAction.UPDATE, "Update users"),
        user(RbacAction.DELETE, "Delete users"),
        permission(RbacAction.ASSIGN, RbacModule.USER_ROLE, "Assign roles to users"),
        permission(RbacAction.REMOVE, RbacModule.USER_ROLE, "Remove roles from users"),
        user(RbacAction.RESET_PASSWORD, "Reset user passwords"),
        user(RbacAction.ACTIVATE, "Activate users"),
        user(RbacAction.DEACTIVATE, "Deactivate users"),
        user(RbacAction.VIEW_PERMISSION, "View effective user permissions"),
        role(RbacAction.LIST, "List roles"),
        role(RbacAction.VIEW, "View role details"),
        role(RbacAction.CREATE, "Create roles"),
        role(RbacAction.UPDATE, "Update roles"),
        role(RbacAction.DELETE, "Delete roles"),
        permission(RbacAction.ASSIGN, RbacModule.ROLE_PERMISSION, "Assign permissions to roles"),
        permission(RbacAction.REMOVE, RbacModule.ROLE_PERMISSION, "Remove permissions from roles"),
        permission(RbacAction.LIST, RbacModule.PERMISSION, "List permissions"),
        permission(RbacAction.VIEW, RbacModule.PERMISSION, "View permission details"),
        permission(RbacAction.CREATE, RbacModule.PERMISSION, "Create permissions"),
        permission(RbacAction.UPDATE, RbacModule.PERMISSION, "Update permissions"),
        permission(RbacAction.DELETE, RbacModule.PERMISSION, "Delete permissions"),
        permission(
            RbacAction.VIEW_DELETED,
            RbacModule.PERMISSION,
            "Allows viewing inactive/deleted permissions"),
        permission(
            RbacAction.ASSIGN, RbacModule.PERMISSION_DEPENDENCY, "Assign permission dependencies"),
        permission(
            RbacAction.REMOVE, RbacModule.PERMISSION_DEPENDENCY, "Remove permission dependencies"),
        menu(RbacAction.LIST, "List menus"),
        menu(RbacAction.VIEW, "View menu details"),
        menu(RbacAction.CREATE, "Create menus"),
        menu(RbacAction.UPDATE, "Update menus"),
        menu(RbacAction.DELETE, "Delete menus"),
        menu(RbacAction.REORDER, "Reorder menus"),
        menu(RbacAction.ACTIVATE, "Activate menus"),
        menu(RbacAction.DEACTIVATE, "Deactivate menus"),
        audit(RbacAction.LIST, "List audit logs"),
        audit(RbacAction.VIEW, "View audit log details"));
  }

  private static CorePermissionSeed user(RbacAction action, String description) {
    return seed(AuthorityNames.userAuthority(action), RbacModule.USER, action, description);
  }

  private static CorePermissionSeed role(RbacAction action, String description) {
    return seed(
        AuthorityNames.authority(action, RbacModule.ROLE), RbacModule.ROLE, action, description);
  }

  private static CorePermissionSeed permission(
      RbacAction action, RbacModule module, String description) {
    return seed(AuthorityNames.authority(action, module), module, action, description);
  }

  private static CorePermissionSeed menu(RbacAction action, String description) {
    return seed(
        AuthorityNames.authority(action, RbacModule.MENU), RbacModule.MENU, action, description);
  }

  private static CorePermissionSeed audit(RbacAction action, String description) {
    return seed(
        AuthorityNames.authority(action, RbacModule.AUDIT_LOG),
        RbacModule.AUDIT_LOG,
        action,
        description);
  }

  private static CorePermissionSeed seed(
      String name, RbacModule module, RbacAction action, String description) {
    return new CorePermissionSeed(name, module.name(), action.name(), description);
  }
}
