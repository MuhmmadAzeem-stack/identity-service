package com.omnicore.identity.permission;

public final class PermissionValidationMessages {

    public static final String NAME_BLANK = "Permission name must not be blank";
    public static final String ACTION_BLANK = "Permission action must not be blank";
    public static final String MODULE_BLANK = "Permission module must not be blank";
    public static final String NAME_FORMAT_MISMATCH_PREFIX =
        "Permission name must match format ACTION_MODULE. Expected: ";
    public static final String NAME_CONTAINS_SPACES = "Permission name must not contain spaces";
    public static final String DESCRIPTION_TOO_LONG_PREFIX = "Permission description must not exceed ";
    public static final String DESCRIPTION_TOO_LONG_SUFFIX = " characters";

    private PermissionValidationMessages() {
    }
}
