package com.omnicore.identity.security;

public final class SecurityExpressions {

  public static final String HAS_LIST_PERMISSION = "hasAuthority('LIST_PERMISSION')";
  public static final String HAS_VIEW_PERMISSION = "hasAuthority('VIEW_PERMISSION')";
  public static final String HAS_CREATE_PERMISSION = "hasAuthority('CREATE_PERMISSION')";
  public static final String HAS_UPDATE_PERMISSION = "hasAuthority('UPDATE_PERMISSION')";
  public static final String HAS_DELETE_PERMISSION = "hasAuthority('DELETE_PERMISSION')";
  public static final String HAS_LIST_ROLE = "hasAuthority('LIST_ROLE')";
  public static final String HAS_VIEW_ROLE = "hasAuthority('VIEW_ROLE')";
  public static final String HAS_CREATE_ROLE = "hasAuthority('CREATE_ROLE')";
  public static final String HAS_UPDATE_ROLE = "hasAuthority('UPDATE_ROLE')";
  public static final String HAS_DELETE_ROLE = "hasAuthority('DELETE_ROLE')";
  public static final String HAS_ASSIGN_USER_ROLE = "hasAuthority('ASSIGN_USER_ROLE')";
  public static final String HAS_REMOVE_USER_ROLE = "hasAuthority('REMOVE_USER_ROLE')";
  public static final String HAS_ASSIGN_ROLE_PERMISSION = "hasAuthority('ASSIGN_ROLE_PERMISSION')";
  public static final String HAS_REMOVE_ROLE_PERMISSION = "hasAuthority('REMOVE_ROLE_PERMISSION')";

  private SecurityExpressions() {}
}
