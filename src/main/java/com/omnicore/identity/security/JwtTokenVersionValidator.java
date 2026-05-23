package com.omnicore.identity.security;

import com.omnicore.identity.user.User;
import com.omnicore.identity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenVersionValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_TOKEN_VERSION = new OAuth2Error(
        "invalid_token",
        "Token version is invalid",
        null
    );

    private static final OAuth2Error INVALID_USER = new OAuth2Error(
        "invalid_token",
        "User is no longer active",
        null
    );

    private final UserRepository userRepository;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        Long userId;
        try {
            userId = Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException ex) {
            return OAuth2TokenValidatorResult.failure(INVALID_TOKEN_VERSION);
        }

        Number claimTokenVersion = jwt.getClaim("tokenVersion");
        if (claimTokenVersion == null) {
            return OAuth2TokenValidatorResult.failure(INVALID_TOKEN_VERSION);
        }

        int tokenVersion = claimTokenVersion.intValue();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !user.isActive() || user.isDeleted()) {
            return OAuth2TokenValidatorResult.failure(INVALID_USER);
        }

        if (!TokenVersionSupport.isValid(user.getTokenVersion())
            || user.getTokenVersion() != tokenVersion) {
            return OAuth2TokenValidatorResult.failure(INVALID_TOKEN_VERSION);
        }

        return OAuth2TokenValidatorResult.success();
    }
}
