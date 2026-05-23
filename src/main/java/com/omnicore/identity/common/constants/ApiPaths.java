package com.omnicore.identity.common.constants;

public final class ApiPaths {

    public static final String API_V1 = "/api/v1";

    /**
     * Blocks unversioned {@code /api/**} routes that are not covered by {@link #API_V1}.
     */
    public static final String LEGACY_API_PREFIX = "/api";

    public static final String AUTH_BASE_PATH = API_V1 + "/auth";
    public static final String PERMISSIONS_BASE_PATH = API_V1 + "/permissions";
    public static final String ROLES_BASE_PATH = API_V1 + "/roles";
    public static final String USERS_BASE_PATH = API_V1 + "/users";
    public static final String MENUS_BASE_PATH = API_V1 + "/menus";
    public static final String AUDIT_LOGS_BASE_PATH = API_V1 + "/audit-logs";

    public static final String LOGIN_PATH = "/login";
    public static final String ME_PATH = "/me";

    private ApiPaths() {
    }
}
