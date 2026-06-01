package com.omnicore.identity.security;

public final class SecurityExpressions {

  public static final String HAS_LIST_PERMISSION = "hasAuthority('LIST_PERMISSION')";
  public static final String HAS_VIEW_PERMISSION = "hasAuthority('VIEW_PERMISSION')";
  public static final String HAS_CREATE_PERMISSION = "hasAuthority('CREATE_PERMISSION')";
  public static final String HAS_UPDATE_PERMISSION = "hasAuthority('UPDATE_PERMISSION')";

  private SecurityExpressions() {}
}
