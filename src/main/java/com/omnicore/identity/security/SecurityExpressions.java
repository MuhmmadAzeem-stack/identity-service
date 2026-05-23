package com.omnicore.identity.security;

public final class SecurityExpressions {

    public static final String HAS_LIST_PERMISSION = "hasAuthority('LIST_PERMISSION')";
    public static final String HAS_VIEW_PERMISSION = "hasAuthority('VIEW_PERMISSION')";

    private SecurityExpressions() {
    }
}
