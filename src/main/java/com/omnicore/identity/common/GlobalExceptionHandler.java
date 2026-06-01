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
          case PERMISSION_ALREADY_EXISTS -> HttpStatus.CONFLICT;
          case SYSTEM_PERMISSION_UPDATE_RESTRICTED -> HttpStatus.FORBIDDEN;
          default -> HttpStatus.BAD_REQUEST;
        };

    return ResponseEntity.status(status)
        .body(
            ApiResponse.failure(
                messageResolver.resolve(resolvePermissionMessageKey(ex.getErrorCode())),
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

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST, "Invalid request parameter: " + ex.getName());
  }

  private String resolvePermissionMessageKey(PermissionErrorCode errorCode) {
    return switch (errorCode) {
      case PERMISSION_ALREADY_EXISTS -> MessageKeys.PERMISSION_ALREADY_EXISTS;
      case PERMISSION_INACTIVE -> MessageKeys.PERMISSION_INACTIVE;
      case INVALID_PERMISSION_NAME_FORMAT -> MessageKeys.INVALID_PERMISSION_NAME_FORMAT;
      case SYSTEM_PERMISSION_UPDATE_RESTRICTED -> MessageKeys.SYSTEM_PERMISSION_UPDATE_RESTRICTED;
      case DEPENDENCY_PERMISSION_NOT_FOUND -> MessageKeys.DEPENDENCY_PERMISSION_NOT_FOUND;
      case DEPENDENCY_PERMISSION_INACTIVE -> MessageKeys.DEPENDENCY_PERMISSION_INACTIVE;
      case DUPLICATE_DEPENDENCY_PERMISSION -> MessageKeys.DUPLICATE_DEPENDENCY_PERMISSION;
      case PERMISSION_SELF_DEPENDENCY_NOT_ALLOWED ->
          MessageKeys.PERMISSION_SELF_DEPENDENCY_NOT_ALLOWED;
      case PERMISSION_CIRCULAR_DEPENDENCY -> MessageKeys.PERMISSION_CIRCULAR_DEPENDENCY;
      default -> errorCode.name();
    };
  }
}
