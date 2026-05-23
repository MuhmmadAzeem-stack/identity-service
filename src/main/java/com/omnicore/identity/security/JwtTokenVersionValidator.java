package com.omnicore.identity.security;

import com.omnicore.identity.common.MessageResolver;
import com.omnicore.identity.common.constants.JwtClaims;
import com.omnicore.identity.common.constants.MessageKeys;
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

    private final UserRepository userRepository;
    private final MessageResolver messageResolver;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        Long userId;
        try {
            userId = Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException ex) {
            return OAuth2TokenValidatorResult.failure(invalidTokenVersionError());
        }

        Number claimTokenVersion = jwt.getClaim(JwtClaims.CLAIM_TOKEN_VERSION);
        if (claimTokenVersion == null) {
            return OAuth2TokenValidatorResult.failure(invalidTokenVersionError());
        }

        int tokenVersion = claimTokenVersion.intValue();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !user.isActive() || user.isDeleted()) {
            return OAuth2TokenValidatorResult.failure(invalidUserError());
        }

        if (!TokenVersionSupport.isValid(user.getTokenVersion())
            || user.getTokenVersion() != tokenVersion) {
            return OAuth2TokenValidatorResult.failure(invalidTokenVersionError());
        }

        return OAuth2TokenValidatorResult.success();
    }

    private OAuth2Error invalidTokenVersionError() {
        return new OAuth2Error(
            JwtClaims.INVALID_TOKEN_VERSION_CODE,
            messageResolver.resolve(MessageKeys.TOKEN_VERSION_INVALID),
            null
        );
    }

    private OAuth2Error invalidUserError() {
        return new OAuth2Error(
            JwtClaims.INVALID_TOKEN_VERSION_CODE,
            messageResolver.resolve(MessageKeys.USER_NO_LONGER_ACTIVE),
            null
        );
    }
}
