package com.omnicore.identity.permission.error;

public class PermissionBusinessException extends RuntimeException {

  private final PermissionErrorCode errorCode;

  public PermissionBusinessException(PermissionErrorCode errorCode) {
    super(errorCode.name());
    this.errorCode = errorCode;
  }

  public PermissionErrorCode getErrorCode() {
    return errorCode;
  }
}
