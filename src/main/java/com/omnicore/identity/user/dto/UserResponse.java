package com.omnicore.identity.user.dto;

import java.util.List;

public record UserResponse(Long id, String email, String firstName, String lastName, boolean isActive,
                           List<String> roles) {
}
