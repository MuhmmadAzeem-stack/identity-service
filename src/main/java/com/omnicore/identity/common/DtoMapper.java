package com.omnicore.identity.common;

import com.omnicore.identity.role.Role;
import com.omnicore.identity.user.User;
import com.omnicore.identity.user.dto.UserResponse;

public final class DtoMapper {
    private DtoMapper(){

    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isActive(),
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .sorted()
                        .toList()
        );
    }
}
