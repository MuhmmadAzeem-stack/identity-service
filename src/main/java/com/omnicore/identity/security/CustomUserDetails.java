package com.omnicore.identity.security;

import com.omnicore.identity.user.User;
import lombok.Getter;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;

        Set<GrantedAuthority> auths = new HashSet<>();

        user.getRoles().forEach(role -> {
            auths.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));

            role.getPermissions().forEach(permission ->
                auths.add(new SimpleGrantedAuthority(permission.getCode()))
            );
        });

        this.authorities = auths;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().name().equals("ACTIVE");
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getStatus().name().equals("LOCKED");
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}