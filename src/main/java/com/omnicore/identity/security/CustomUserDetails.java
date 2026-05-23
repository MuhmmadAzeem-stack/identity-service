package com.omnicore.identity.security;

import com.omnicore.identity.permission.EffectivePermissionResolver;
import com.omnicore.identity.role.Role;
import com.omnicore.identity.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user, EffectivePermissionResolver effectivePermissionResolver) {
        this.user = user;

        Set<GrantedAuthority> auths = new HashSet<>();

        for (Role role : user.getRoles()) {
            if (role.isActive() && !role.isDeleted()) {
                auths.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
        }

        effectivePermissionResolver.resolvePermissionNames(user).stream()
            .map(SimpleGrantedAuthority::new)
            .forEach(auths::add);

        this.authorities = auths;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive() && !user.isDeleted();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
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
