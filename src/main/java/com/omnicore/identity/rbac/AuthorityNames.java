package com.omnicore.identity.rbac;

public final class AuthorityNames {

    private static final String RESET_USER_PASSWORD_VALUE = "RESET_USER_PASSWORD";
    private static final String VIEW_USER_PERMISSION_VALUE = "VIEW_USER_PERMISSION";

    public static final String LIST_USER = authority(RbacAction.LIST, RbacModule.USER);
    public static final String VIEW_USER = authority(RbacAction.VIEW, RbacModule.USER);
    public static final String CREATE_USER = authority(RbacAction.CREATE, RbacModule.USER);
    public static final String UPDATE_USER = authority(RbacAction.UPDATE, RbacModule.USER);
    public static final String DELETE_USER = authority(RbacAction.DELETE, RbacModule.USER);
    public static final String ASSIGN_USER_ROLE = authority(RbacAction.ASSIGN, RbacModule.USER_ROLE);
    public static final String REMOVE_USER_ROLE = authority(RbacAction.REMOVE, RbacModule.USER_ROLE);
    public static final String RESET_USER_PASSWORD = RESET_USER_PASSWORD_VALUE;
    public static final String ACTIVATE_USER = authority(RbacAction.ACTIVATE, RbacModule.USER);
    public static final String DEACTIVATE_USER = authority(RbacAction.DEACTIVATE, RbacModule.USER);
    public static final String VIEW_USER_PERMISSION = VIEW_USER_PERMISSION_VALUE;

    public static final String LIST_ROLE = authority(RbacAction.LIST, RbacModule.ROLE);
    public static final String VIEW_ROLE = authority(RbacAction.VIEW, RbacModule.ROLE);
    public static final String CREATE_ROLE = authority(RbacAction.CREATE, RbacModule.ROLE);
    public static final String UPDATE_ROLE = authority(RbacAction.UPDATE, RbacModule.ROLE);
    public static final String DELETE_ROLE = authority(RbacAction.DELETE, RbacModule.ROLE);
    public static final String ASSIGN_ROLE_PERMISSION = authority(RbacAction.ASSIGN, RbacModule.ROLE_PERMISSION);
    public static final String REMOVE_ROLE_PERMISSION = authority(RbacAction.REMOVE, RbacModule.ROLE_PERMISSION);

    public static final String LIST_PERMISSION = authority(RbacAction.LIST, RbacModule.PERMISSION);
    public static final String VIEW_PERMISSION = authority(RbacAction.VIEW, RbacModule.PERMISSION);
    public static final String CREATE_PERMISSION = authority(RbacAction.CREATE, RbacModule.PERMISSION);
    public static final String UPDATE_PERMISSION = authority(RbacAction.UPDATE, RbacModule.PERMISSION);
    public static final String DELETE_PERMISSION = authority(RbacAction.DELETE, RbacModule.PERMISSION);
    public static final String ASSIGN_PERMISSION_DEPENDENCY =
        authority(RbacAction.ASSIGN, RbacModule.PERMISSION_DEPENDENCY);
    public static final String REMOVE_PERMISSION_DEPENDENCY =
        authority(RbacAction.REMOVE, RbacModule.PERMISSION_DEPENDENCY);

    public static final String LIST_MENU = authority(RbacAction.LIST, RbacModule.MENU);
    public static final String VIEW_MENU = authority(RbacAction.VIEW, RbacModule.MENU);
    public static final String CREATE_MENU = authority(RbacAction.CREATE, RbacModule.MENU);
    public static final String UPDATE_MENU = authority(RbacAction.UPDATE, RbacModule.MENU);
    public static final String DELETE_MENU = authority(RbacAction.DELETE, RbacModule.MENU);
    public static final String REORDER_MENU = authority(RbacAction.REORDER, RbacModule.MENU);
    public static final String ACTIVATE_MENU = authority(RbacAction.ACTIVATE, RbacModule.MENU);
    public static final String DEACTIVATE_MENU = authority(RbacAction.DEACTIVATE, RbacModule.MENU);

    public static final String LIST_AUDIT_LOG = authority(RbacAction.LIST, RbacModule.AUDIT_LOG);
    public static final String VIEW_AUDIT_LOG = authority(RbacAction.VIEW, RbacModule.AUDIT_LOG);

    private AuthorityNames() {
    }

    public static String authority(RbacAction action, RbacModule module) {
        return action.name() + "_" + module.name();
    }

    /**
     * USER module permissions that do not follow {@code ACTION_MODULE} naming.
     */
    public static String userAuthority(RbacAction action) {
        return switch (action) {
            case RESET_PASSWORD -> RESET_USER_PASSWORD_VALUE;
            case VIEW_PERMISSION -> VIEW_USER_PERMISSION_VALUE;
            default -> authority(action, RbacModule.USER);
        };
    }
}
