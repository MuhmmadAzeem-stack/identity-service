package com.omnicore.identity.auth;

import com.omnicore.identity.auth.dto.LoginResponse;
import com.omnicore.identity.auth.dto.MeResponse;
import com.omnicore.identity.common.constants.ApiPaths;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.AUTH_BASE_PATH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(ApiPaths.LOGIN_PATH)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping(ApiPaths.ME_PATH)
    public MeResponse me(@AuthenticationPrincipal Jwt jwt) {
        return authService.getCurrentUser(Long.parseLong(jwt.getSubject()));
    }
}
