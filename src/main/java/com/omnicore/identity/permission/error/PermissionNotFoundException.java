package com.omnicore.identity.permission.error;

public class PermissionNotFoundException extends RuntimeException {

  private final PermissionErrorCode errorCode;

  public PermissionNotFoundException() {
    super(PermissionErrorCode.PERMISSION_NOT_FOUND.name());
    this.errorCode = PermissionErrorCode.PERMISSION_NOT_FOUND;
  }

  public PermissionErrorCode getErrorCode() {
    return errorCode;
  }
}
