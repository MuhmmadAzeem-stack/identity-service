package com.omnicore.identity.seed;

import java.util.List;

public final class CorePermissionDependencySeeds {

    private CorePermissionDependencySeeds() {
    }

    public static List<PermissionDependencySeed> all() {
        return List.of(
            dep("CREATE_USER", "LIST_ROLE"),
            dep("UPDATE_USER", "VIEW_USER"),
            dep("UPDATE_USER", "LIST_ROLE"),
            dep("DELETE_USER", "VIEW_USER"),
            dep("ASSIGN_USER_ROLE", "LIST_ROLE"),
            dep("VIEW_USER_PERMISSION", "VIEW_USER"),

            dep("CREATE_ROLE", "LIST_PERMISSION"),
            dep("UPDATE_ROLE", "VIEW_ROLE"),
            dep("UPDATE_ROLE", "LIST_PERMISSION"),
            dep("DELETE_ROLE", "VIEW_ROLE"),
            dep("ASSIGN_ROLE_PERMISSION", "LIST_PERMISSION"),

            dep("CREATE_PERMISSION", "LIST_PERMISSION"),
            dep("UPDATE_PERMISSION", "VIEW_PERMISSION"),
            dep("DELETE_PERMISSION", "VIEW_PERMISSION"),
            dep("ASSIGN_PERMISSION_DEPENDENCY", "LIST_PERMISSION"),
            dep("REMOVE_PERMISSION_DEPENDENCY", "LIST_PERMISSION"),

            dep("CREATE_MENU", "LIST_PERMISSION"),
            dep("UPDATE_MENU", "VIEW_MENU"),
            dep("UPDATE_MENU", "LIST_PERMISSION"),
            dep("DELETE_MENU", "VIEW_MENU"),
            dep("REORDER_MENU", "VIEW_MENU"),
            dep("ACTIVATE_MENU", "VIEW_MENU"),
            dep("DEACTIVATE_MENU", "VIEW_MENU"),

            dep("VIEW_AUDIT_LOG", "LIST_AUDIT_LOG")
        );
    }

    private static PermissionDependencySeed dep(String permission, String dependsOn) {
        return new PermissionDependencySeed(permission, dependsOn);
    }
}
