package com.omnicore.identity.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        return new MeResponse(
                jwt.getSubject(),
                jwt.getClaimAsString("username"),
                jwt.getClaimAsString("email"),
                getClaimAsList(jwt, "roles"),
                getClaimAsList(jwt, "permissions"),
                jwt.getIssuedAt(),
                jwt.getExpiresAt()
        );
    }

    private List<String> getClaimAsList(Jwt jwt, String claimName) {
        List<String> claim = jwt.getClaimAsStringList(claimName);
        return claim != null ? claim : List.of();
    }
}