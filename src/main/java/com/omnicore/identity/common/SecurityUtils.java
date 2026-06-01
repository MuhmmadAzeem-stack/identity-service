package com.omnicore.identity.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.omnicore.identity.common.constants.MessageKeys;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

  private final MessageResolver messageResolver;

  public Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
      throw new IllegalStateException(messageResolver.resolve(MessageKeys.NO_AUTHENTICATED_USER));
    }
    return Long.parseLong(jwt.getSubject());
  }
}
