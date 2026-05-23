package com.omnicore.identity.seed;

import java.util.List;

public final class CorePermissionSeeds {

    public static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    private CorePermissionSeeds() {
    }

    public static List<CorePermissionSeed> all() {
        return List.of(
            user("LIST_USER", "LIST", "List users"),
            user("VIEW_USER", "VIEW", "View user details"),
            user("CREATE_USER", "CREATE", "Create users"),
            user("UPDATE_USER", "UPDATE", "Update users"),
            user("DELETE_USER", "DELETE", "Delete users"),
            permission("ASSIGN_USER_ROLE", "USER_ROLE", "ASSIGN", "Assign roles to users"),
            permission("REMOVE_USER_ROLE", "USER_ROLE", "REMOVE", "Remove roles from users"),
            user("RESET_USER_PASSWORD", "RESET_PASSWORD", "Reset user passwords"),
            user("ACTIVATE_USER", "ACTIVATE", "Activate users"),
            user("DEACTIVATE_USER", "DEACTIVATE", "Deactivate users"),
            user("VIEW_USER_PERMISSION", "VIEW_PERMISSION", "View effective user permissions"),

            role("LIST_ROLE", "LIST", "List roles"),
            role("VIEW_ROLE", "VIEW", "View role details"),
            role("CREATE_ROLE", "CREATE", "Create roles"),
            role("UPDATE_ROLE", "UPDATE", "Update roles"),
            role("DELETE_ROLE", "DELETE", "Delete roles"),
            permission("ASSIGN_ROLE_PERMISSION", "ROLE_PERMISSION", "ASSIGN", "Assign permissions to roles"),
            permission("REMOVE_ROLE_PERMISSION", "ROLE_PERMISSION", "REMOVE", "Remove permissions from roles"),

            permission("LIST_PERMISSION", "PERMISSION", "LIST", "List permissions"),
            permission("VIEW_PERMISSION", "PERMISSION", "VIEW", "View permission details"),
            permission("CREATE_PERMISSION", "PERMISSION", "CREATE", "Create permissions"),
            permission("UPDATE_PERMISSION", "PERMISSION", "UPDATE", "Update permissions"),
            permission("DELETE_PERMISSION", "PERMISSION", "DELETE", "Delete permissions"),
            permission("ASSIGN_PERMISSION_DEPENDENCY", "PERMISSION_DEPENDENCY", "ASSIGN", "Assign permission dependencies"),
            permission("REMOVE_PERMISSION_DEPENDENCY", "PERMISSION_DEPENDENCY", "REMOVE", "Remove permission dependencies"),

            menu("LIST_MENU", "LIST", "List menus"),
            menu("VIEW_MENU", "VIEW", "View menu details"),
            menu("CREATE_MENU", "CREATE", "Create menus"),
            menu("UPDATE_MENU", "UPDATE", "Update menus"),
            menu("DELETE_MENU", "DELETE", "Delete menus"),
            menu("REORDER_MENU", "REORDER", "Reorder menus"),
            menu("ACTIVATE_MENU", "ACTIVATE", "Activate menus"),
            menu("DEACTIVATE_MENU", "DEACTIVATE", "Deactivate menus"),

            audit("LIST_AUDIT_LOG", "LIST", "List audit logs"),
            audit("VIEW_AUDIT_LOG", "VIEW", "View audit log details")
        );
    }

    private static CorePermissionSeed user(String name, String action, String description) {
        return new CorePermissionSeed(name, "USER", action, description);
    }

    private static CorePermissionSeed role(String name, String action, String description) {
        return new CorePermissionSeed(name, "ROLE", action, description);
    }

    private static CorePermissionSeed permission(String name, String module, String action, String description) {
        return new CorePermissionSeed(name, module, action, description);
    }

    private static CorePermissionSeed menu(String name, String action, String description) {
        return new CorePermissionSeed(name, "MENU", action, description);
    }

    private static CorePermissionSeed audit(String name, String action, String description) {
        return new CorePermissionSeed(name, "AUDIT_LOG", action, description);
    }
}
