package com.omnicore.identity.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import com.omnicore.identity.common.constants.JwtClaims;
import com.omnicore.identity.common.constants.SecurityConstants;

@Component
public class JwtPermissionAuthenticationConverter
    implements Converter<Jwt, AbstractAuthenticationToken> {

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = new ArrayList<>();

    List<String> roles = jwt.getClaimAsStringList(JwtClaims.CLAIM_ROLES);
    if (roles != null) {
      roles.stream()
          .map(role -> new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + role))
          .forEach(authorities::add);
    }

    List<String> permissions = jwt.getClaimAsStringList(JwtClaims.CLAIM_PERMISSIONS);
    if (permissions != null) {
      permissions.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
    }

    return new JwtAuthenticationToken(
        jwt, authorities, jwt.getClaimAsString(JwtClaims.CLAIM_EMAIL));
  }
}
