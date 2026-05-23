package com.omnicore.identity.security;

import com.omnicore.identity.permission.EffectivePermissionResolver;
import com.omnicore.identity.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final EffectivePermissionResolver effectivePermissionResolver;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.access-token-minutes}")
    private long accessTokenMinutes;

    public String generateToken(CustomUserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(accessTokenMinutes * 60);

        Set<String> roles = userDetails.getUser().getRoles().stream()
            .filter(role -> role.isActive() && !role.isDeleted())
            .map(Role::getName)
            .collect(Collectors.toSet());

        Set<String> permissions = effectivePermissionResolver.resolvePermissionNames(userDetails.getUser());

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(String.valueOf(userDetails.getUser().getId()))
            .claim("email", userDetails.getUser().getEmail())
            .claim("roles", roles)
            .claim("permissions", permissions)
            .claim("tokenVersion", userDetails.getUser().getTokenVersion())
            .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder
            .encode(JwtEncoderParameters.from(header, claims))
            .getTokenValue();
    }

    public long getExpiresInSeconds() {
        return accessTokenMinutes * 60;
    }
}
