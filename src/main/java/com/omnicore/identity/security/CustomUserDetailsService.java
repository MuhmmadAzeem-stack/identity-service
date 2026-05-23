package com.omnicore.identity.security;

import com.omnicore.identity.common.MessageResolver;
import com.omnicore.identity.common.constants.JwtClaims;
import com.omnicore.identity.common.constants.MessageKeys;
import com.omnicore.identity.permission.EffectivePermissionResolver;
import com.omnicore.identity.user.User;
import com.omnicore.identity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EffectivePermissionResolver effectivePermissionResolver;
    private final MessageResolver messageResolver;

    @Override
    public UserDetails loadUserByUsername(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmailAndDeletedAtIsNull(normalizedEmail)
            .orElseThrow(() -> new UsernameNotFoundException(
                messageResolver.resolve(MessageKeys.INVALID_EMAIL_OR_PASSWORD)));

        if (!user.isActive()) {
            throw new DisabledException(messageResolver.resolve(MessageKeys.USER_ACCOUNT_INACTIVE));
        }

        if (!TokenVersionSupport.isValid(user.getTokenVersion())) {
            throw new InvalidTokenVersionException(messageResolver.resolve(MessageKeys.TOKEN_VERSION_INVALID));
        }

        return new CustomUserDetails(user, effectivePermissionResolver);
    }
}
