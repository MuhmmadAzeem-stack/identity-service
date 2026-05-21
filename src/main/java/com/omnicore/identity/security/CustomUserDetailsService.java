package com.omnicore.identity.security;

import com.omnicore.identity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        return userRepository
            .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    }
}