package com.omnicore.identity.security;

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

    @Override
    public UserDetails loadUserByUsername(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmailAndDeletedAtIsNull(normalizedEmail)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        if (!user.isActive()) {
            throw new DisabledException("User account is inactive");
        }

        if (!TokenVersionSupport.isValid(user.getTokenVersion())) {
            throw new InvalidTokenVersionException("Token version is invalid");
        }

        return new CustomUserDetails(user, effectivePermissionResolver);
    }
}
