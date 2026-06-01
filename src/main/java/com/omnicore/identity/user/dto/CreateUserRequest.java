package com.omnicore.identity.user.dto;

import com.omnicore.identity.role.Role;

import java.util.Set;

public record CreateUserRequest(String email, String firstName, String lastName, String password, boolean isActive, Set<Role> roleIds) {
}
