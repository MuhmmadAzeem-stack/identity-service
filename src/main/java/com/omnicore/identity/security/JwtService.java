package com.omnicore.identity.security;

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

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.access-token-minutes}")
    private long accessTokenMinutes;

    public String generateToken(CustomUserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(accessTokenMinutes * 60);

        Set<String> roles = userDetails.getUser().getRoles()
                .stream()
                .map(role -> role.getCode())
                .collect(Collectors.toSet());

        Set<String> permissions = userDetails.getUser().getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getCode())
                .collect(Collectors.toSet());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(String.valueOf(userDetails.getUser().getId()))
                .claim("username", userDetails.getUser().getUsername())
                .claim("email", userDetails.getUser().getEmail())
                .claim("roles", roles)
                .claim("permissions", permissions)
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