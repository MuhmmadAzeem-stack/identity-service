package com.omnicore.identity.rbac;

public final class RbacConstants {

  public static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
  public static final String SUPER_ADMIN_DESCRIPTION = "Full system access";

  /** Initial token version assigned to the seeded super admin user. */
  public static final int INITIAL_SUPER_ADMIN_TOKEN_VERSION = 1;

  private RbacConstants() {}
}
