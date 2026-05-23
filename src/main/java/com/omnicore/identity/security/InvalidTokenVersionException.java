package com.omnicore.identity.security;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenVersionException extends AuthenticationException {

    public InvalidTokenVersionException(String message) {
        super(message);
    }
}
