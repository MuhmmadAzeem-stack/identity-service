package com.omnicore.identity.common.constants;

public final class SeedConstants {

  public static final String RBAC_SEED_PREFIX = "app.rbac.seed";
  public static final String RBAC_SEED_ENABLED_PROPERTY = RBAC_SEED_PREFIX + ".enabled";
  public static final String INITIAL_SUPER_ADMIN_PASSWORD_ENV = "INITIAL_SUPER_ADMIN_PASSWORD";

  public static final String RBAC_SEED_START_LOG = "Starting RBAC data seed";
  public static final String RBAC_SEED_COMPLETED_LOG = "RBAC data seed completed";
  public static final String DEV_PASSWORD_NOT_SET_LOG =
      "INITIAL_SUPER_ADMIN_PASSWORD is not set; using configured development default password. "
          + "Set INITIAL_SUPER_ADMIN_PASSWORD for non-development environments.";
  public static final String INITIAL_SUPER_ADMIN_PASSWORD_REQUIRED =
      "INITIAL_SUPER_ADMIN_PASSWORD must be set when app.rbac.seed.allow-dev-default-password=false";
  public static final String DEV_DEFAULT_PASSWORD_REQUIRED =
      "app.rbac.seed.dev-default-password must be configured when app.rbac.seed.allow-dev-default-password=true";

  public static final String PERMISSION_SELF_DEPENDENCY_PREFIX =
      "Permission dependency cannot reference itself: ";
  public static final String CIRCULAR_DEPENDENCY_PREFIX =
      "Circular permission dependency detected: ";
  public static final String MISSING_SEEDED_PERMISSION_PREFIX =
      "Missing seeded permission required for dependency: ";

  private SeedConstants() {}
}
