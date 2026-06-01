package com.omnicore.identity.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.omnicore.identity.common.constants.MessageKeys;
import com.omnicore.identity.permission.error.PermissionBusinessException;
import com.omnicore.identity.permission.error.PermissionErrorCode;
import com.omnicore.identity.permission.error.PermissionNotFoundException;
import com.omnicore.identity.role.error.RoleBusinessException;
import com.omnicore.identity.role.error.RoleErrorCode;
import com.omnicore.identity.role.error.RoleNotFoundException;
import com.omnicore.identity.security.InvalidTokenVersionException;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final MessageResolver messageResolver;

  @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
  public ProblemDetail handleBadCredentials(Exception ex) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, messageResolver.resolve(MessageKeys.INVALID_EMAIL_OR_PASSWORD));
  }

  @ExceptionHandler(DisabledException.class)
  public ProblemDetail handleDisabled(DisabledException ex) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, messageResolver.resolve(MessageKeys.USER_ACCOUNT_INACTIVE));
  }

  @ExceptionHandler(InvalidTokenVersionException.class)
  public ProblemDetail handleInvalidTokenVersion(InvalidTokenVersionException ex) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, messageResolver.resolve(MessageKeys.TOKEN_VERSION_INVALID));
  }

  @ExceptionHandler(PermissionNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handlePermissionNotFound(
      PermissionNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            ApiResponse.failure(
                messageResolver.resolve(MessageKeys.PERMISSION_NOT_FOUND),
                PermissionErrorCode.PERMISSION_NOT_FOUND.name()));
  }

  @ExceptionHandler(PermissionBusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handlePermissionBusiness(
      PermissionBusinessException ex) {
    HttpStatus status =
        switch (ex.getErrorCode()) {
          case PERMISSION_ALREADY_EXISTS,
              PERMISSION_INACTIVE,
              PERMISSION_ALREADY_ACTIVE,
              PERMISSION_ASSIGNED_TO_ROLE,
              PERMISSION_USED_AS_DEPENDENCY -> HttpStatus.CONFLICT;
          case SYSTEM_PERMISSION_UPDATE_RESTRICTED,
              SYSTEM_PERMISSION_DELETE_NOT_ALLOWED,
              VIEW_DELETED_PERMISSION_REQUIRED -> HttpStatus.FORBIDDEN;
          default -> HttpStatus.BAD_REQUEST;
        };

    return ResponseEntity.status(status)
        .body(
            ApiResponse.failure(
                messageResolver.resolve(resolvePermissionMessageKey(ex.getErrorCode())),
                ex.getErrorCode().name()));
  }

  @ExceptionHandler(RoleNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleRoleNotFound(RoleNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            ApiResponse.failure(
                messageResolver.resolve(MessageKeys.ROLE_NOT_FOUND),
                RoleErrorCode.ROLE_NOT_FOUND.name()));
  }

  @ExceptionHandler(RoleBusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleRoleBusiness(RoleBusinessException ex) {
    HttpStatus status =
        switch (ex.getErrorCode()) {
          case ROLE_ALREADY_EXISTS -> HttpStatus.CONFLICT;
          case SYSTEM_ROLE_UPDATE_RESTRICTED, SYSTEM_ROLE_DELETE_RESTRICTED ->
              HttpStatus.FORBIDDEN;
          case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
          default -> HttpStatus.BAD_REQUEST;
        };

    return ResponseEntity.status(status)
        .body(
            ApiResponse.failure(
                messageResolver.resolve(resolveRoleMessageKey(ex.getErrorCode())),
                ex.getErrorCode().name()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ProblemDetail handleIllegalState(IllegalStateException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }


  @ExceptionHandler(ResourceNotFoundException.class)
  public ProblemDetail resourceNotFound(ResourceNotFoundException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
  }
  @ExceptionHandler(BadRequestException.class)
  public ProblemDetail badRequestException(BadRequestException ex){
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, "Invalid request parameter: " + ex.getName());
  }

  private String resolvePermissionMessageKey(PermissionErrorCode errorCode) {
    return switch (errorCode) {
      case PERMISSION_ALREADY_EXISTS -> MessageKeys.PERMISSION_ALREADY_EXISTS;
      case PERMISSION_INACTIVE -> MessageKeys.PERMISSION_INACTIVE;
      case PERMISSION_ALREADY_ACTIVE -> MessageKeys.PERMISSION_ALREADY_ACTIVE;
      case INVALID_PERMISSION_NAME_FORMAT -> MessageKeys.INVALID_PERMISSION_NAME_FORMAT;
      case SYSTEM_PERMISSION_UPDATE_RESTRICTED -> MessageKeys.SYSTEM_PERMISSION_UPDATE_RESTRICTED;
      case SYSTEM_PERMISSION_DELETE_NOT_ALLOWED -> MessageKeys.SYSTEM_PERMISSION_DELETE_NOT_ALLOWED;
      case PERMISSION_ASSIGNED_TO_ROLE -> MessageKeys.PERMISSION_ASSIGNED_TO_ROLE;
      case PERMISSION_USED_AS_DEPENDENCY -> MessageKeys.PERMISSION_USED_AS_DEPENDENCY;
      case VIEW_DELETED_PERMISSION_REQUIRED -> MessageKeys.VIEW_DELETED_PERMISSION_REQUIRED;
      case DEPENDENCY_PERMISSION_NOT_FOUND -> MessageKeys.DEPENDENCY_PERMISSION_NOT_FOUND;
      case DEPENDENCY_PERMISSION_INACTIVE -> MessageKeys.DEPENDENCY_PERMISSION_INACTIVE;
      case DUPLICATE_DEPENDENCY_PERMISSION -> MessageKeys.DUPLICATE_DEPENDENCY_PERMISSION;
      case PERMISSION_SELF_DEPENDENCY_NOT_ALLOWED ->
          MessageKeys.PERMISSION_SELF_DEPENDENCY_NOT_ALLOWED;
      case PERMISSION_CIRCULAR_DEPENDENCY -> MessageKeys.PERMISSION_CIRCULAR_DEPENDENCY;
      default -> errorCode.name();
    };
  }

  private String resolveRoleMessageKey(RoleErrorCode errorCode) {
    return switch (errorCode) {
      case ROLE_ALREADY_EXISTS -> MessageKeys.ROLE_ALREADY_EXISTS;
      case ROLE_INACTIVE -> MessageKeys.ROLE_INACTIVE;
      case ROLE_ASSIGNED_TO_USERS -> MessageKeys.ROLE_ASSIGNED_TO_USERS;
      case SYSTEM_ROLE_UPDATE_RESTRICTED -> MessageKeys.SYSTEM_ROLE_UPDATE_RESTRICTED;
      case SYSTEM_ROLE_DELETE_RESTRICTED -> MessageKeys.SYSTEM_ROLE_DELETE_RESTRICTED;
      case INVALID_ROLE_NAME_FORMAT -> MessageKeys.INVALID_ROLE_NAME_FORMAT;
      case ROLE_DESCRIPTION_TOO_LONG -> MessageKeys.ROLE_DESCRIPTION_TOO_LONG;
      case ROLE_PERMISSION_NOT_FOUND -> MessageKeys.ROLE_PERMISSION_NOT_FOUND;
      case ROLE_PERMISSION_INACTIVE -> MessageKeys.ROLE_PERMISSION_INACTIVE;
      case DUPLICATE_ROLE_PERMISSION -> MessageKeys.DUPLICATE_ROLE_PERMISSION;
      case USER_NOT_FOUND -> MessageKeys.ROLE_USER_NOT_FOUND;
      case USER_INACTIVE -> MessageKeys.ROLE_USER_INACTIVE;
      case DUPLICATE_USER_ROLE -> MessageKeys.DUPLICATE_USER_ROLE;
      default -> errorCode.name();
    };
  }
}
