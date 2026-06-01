package com.omnicore.identity.role.error;

public class RoleBusinessException extends RuntimeException {

  private final RoleErrorCode errorCode;

  public RoleBusinessException(RoleErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public RoleErrorCode getErrorCode() {
    return errorCode;
  }
}
