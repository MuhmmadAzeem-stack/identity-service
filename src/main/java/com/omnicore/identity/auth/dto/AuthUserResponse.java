package com.omnicore.identity.auth.dto;

import com.omnicore.identity.user.User;

public record AuthUserResponse(Long id, String email, String firstName, String lastName) {

  public static AuthUserResponse from(User user) {
    return new AuthUserResponse(
        user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
  }
}
