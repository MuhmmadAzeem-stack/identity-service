package com.omnicore.identity.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtPermissionAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> permissions = jwt.getClaimAsStringList("permissions");

        var authorities = permissions == null
                ? List.<SimpleGrantedAuthority>of()
                : permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("username"));
    }
}