package com.omnicore.identity.auth;

import com.omnicore.identity.auth.dto.AuthUserResponse;
import com.omnicore.identity.auth.dto.LoginResponse;
import com.omnicore.identity.auth.dto.MeResponse;
import com.omnicore.identity.permission.EffectivePermissionResolver;
import com.omnicore.identity.role.Role;
import com.omnicore.identity.security.CustomUserDetails;
import com.omnicore.identity.security.JwtService;
import com.omnicore.identity.user.User;
import com.omnicore.identity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EffectivePermissionResolver effectivePermissionResolver;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = normalizeEmail(request.usernameOrEmail());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, request.password())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        String accessToken = jwtService.generateToken(userDetails);

        return new LoginResponse(
            accessToken,
            user.getEmail(),
            AuthUserResponse.from(user),
            resolveActiveRoleNames(user),
            new ArrayList<>(effectivePermissionResolver.resolvePermissionNames(user)),
            user.getTokenVersion()
        );
    }

    @Transactional(readOnly = true)
    public MeResponse getCurrentUser(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        return new MeResponse(
            user.getEmail(),
            AuthUserResponse.from(user),
            resolveActiveRoleNames(user),
            new ArrayList<>(effectivePermissionResolver.resolvePermissionNames(user)),
            user.getTokenVersion()
        );
    }

    private List<String> resolveActiveRoleNames(User user) {
        return user.getRoles().stream()
            .filter(role -> role.isActive() && !role.isDeleted())
            .map(Role::getName)
            .sorted()
            .toList();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
