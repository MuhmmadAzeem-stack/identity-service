package com.omnicore.identity.security;

import com.omnicore.identity.common.constants.JwtClaims;

public final class TokenVersionSupport {

  private TokenVersionSupport() {}

  public static boolean isValid(int tokenVersion) {
    return tokenVersion >= JwtClaims.MIN_TOKEN_VERSION;
  }
}
