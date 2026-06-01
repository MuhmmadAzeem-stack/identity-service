package com.omnicore.identity.common.constants;

public final class JwtClaims {

  public static final String CLAIM_TOKEN_VERSION = "tokenVersion";
  public static final String CLAIM_EMAIL = "email";
  public static final String CLAIM_ROLES = "roles";
  public static final String CLAIM_PERMISSIONS = "permissions";

  /** Minimum accepted token version. Tokens below this value are considered invalid. */
  public static final int MIN_TOKEN_VERSION = 1;

  public static final String INVALID_TOKEN_VERSION_CODE = "invalid_token";

  private JwtClaims() {}
}
