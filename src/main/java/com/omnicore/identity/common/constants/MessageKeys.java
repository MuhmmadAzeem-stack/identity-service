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
  public static final String ROLES_FETCHED = "role.list.success";
  public static final String ROLE_FETCHED = "role.detail.success";
  public static final String ROLE_NOT_FOUND = "role.notFound";
  public static final String ROLE_CREATED = "role.create.success";
  public static final String ROLE_REACTIVATED = "role.reactivate.success";
  public static final String ROLE_UPDATED = "role.update.success";
  public static final String ROLE_DELETED = "role.delete.success";
  public static final String ROLE_PERMISSIONS_UPDATED = "role.permissions.update.success";
  public static final String USER_ROLE_ASSIGNED = "role.user.assign.success";
  public static final String USER_ROLE_REMOVED = "role.user.remove.success";
  public static final String USER_ROLES_UPDATED = "role.user.replace.success";
  public static final String ROLE_ALREADY_EXISTS = "role.alreadyExists";
  public static final String ROLE_INACTIVE = "role.inactive";
  public static final String ROLE_ASSIGNED_TO_USERS = "role.assignedToUsers";
  public static final String SYSTEM_ROLE_UPDATE_RESTRICTED = "role.systemUpdateRestricted";
  public static final String SYSTEM_ROLE_DELETE_RESTRICTED = "role.systemDeleteRestricted";
  public static final String INVALID_ROLE_NAME_FORMAT = "role.invalidNameFormat";
  public static final String ROLE_DESCRIPTION_TOO_LONG = "role.descriptionTooLong";
  public static final String ROLE_PERMISSION_NOT_FOUND = "role.permissionNotFound";
  public static final String ROLE_PERMISSION_INACTIVE = "role.permissionInactive";
  public static final String DUPLICATE_ROLE_PERMISSION = "role.duplicatePermission";
  public static final String ROLE_USER_NOT_FOUND = "role.userNotFound";
  public static final String ROLE_USER_INACTIVE = "role.userInactive";
  public static final String DUPLICATE_USER_ROLE = "role.duplicateUserRole";
  public static final String INVALID_EMAIL_OR_PASSWORD = "auth.invalidEmailOrPassword";
  public static final String USER_ACCOUNT_INACTIVE = "auth.userAccountInactive";
  public static final String TOKEN_VERSION_INVALID = "auth.tokenVersionInvalid";
  public static final String USER_NO_LONGER_ACTIVE = "auth.userNoLongerActive";
  public static final String NO_AUTHENTICATED_USER = "auth.noAuthenticatedUser";
  public static final String AUTHENTICATED_USER_NOT_FOUND = "auth.authenticatedUserNotFound";
  public static final String NOT_IMPLEMENTED_YET = "common.notImplementedYet";

  private MessageKeys() {}
}
