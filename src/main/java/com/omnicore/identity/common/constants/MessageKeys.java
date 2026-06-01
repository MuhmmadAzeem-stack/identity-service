package com.omnicore.identity.common.constants;

public final class MessageKeys {

  public static final String PERMISSIONS_FETCHED = "permission.list.success";
  public static final String PERMISSION_FETCHED = "permission.detail.success";
  public static final String PERMISSION_NOT_FOUND = "permission.notFound";
  public static final String PERMISSION_CREATED = "permission.create.success";
  public static final String PERMISSION_REACTIVATED = "permission.reactivate.success";
  public static final String PERMISSION_UPDATED = "permission.update.success";
  public static final String PERMISSION_ALREADY_EXISTS = "permission.alreadyExists";
  public static final String PERMISSION_INACTIVE = "permission.inactive";
  public static final String SYSTEM_PERMISSION_UPDATE_RESTRICTED =
      "permission.systemUpdateRestricted";
  public static final String INVALID_PERMISSION_NAME_FORMAT = "permission.invalidNameFormat";
  public static final String DEPENDENCY_PERMISSION_NOT_FOUND = "permission.dependencyNotFound";
  public static final String DEPENDENCY_PERMISSION_INACTIVE = "permission.dependencyInactive";
  public static final String DUPLICATE_DEPENDENCY_PERMISSION = "permission.duplicateDependency";
  public static final String PERMISSION_SELF_DEPENDENCY_NOT_ALLOWED =
      "permission.selfDependencyNotAllowed";
  public static final String PERMISSION_CIRCULAR_DEPENDENCY = "permission.circularDependency";
  public static final String INVALID_EMAIL_OR_PASSWORD = "auth.invalidEmailOrPassword";
  public static final String USER_ACCOUNT_INACTIVE = "auth.userAccountInactive";
  public static final String TOKEN_VERSION_INVALID = "auth.tokenVersionInvalid";
  public static final String USER_NO_LONGER_ACTIVE = "auth.userNoLongerActive";
  public static final String NO_AUTHENTICATED_USER = "auth.noAuthenticatedUser";
  public static final String AUTHENTICATED_USER_NOT_FOUND = "auth.authenticatedUserNotFound";
  public static final String NOT_IMPLEMENTED_YET = "common.notImplementedYet";

  private MessageKeys() {}
}
