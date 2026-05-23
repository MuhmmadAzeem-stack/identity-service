package com.omnicore.identity.security;

public final class TokenVersionSupport {

    private TokenVersionSupport() {
    }

    public static boolean isValid(int tokenVersion) {
        return tokenVersion >= 1;
    }
}
