package com.omnicore.identity.common;

import com.omnicore.identity.common.constants.MessageKeys;
import com.omnicore.identity.permission.error.PermissionErrorCode;
import com.omnicore.identity.permission.error.PermissionNotFoundException;
import com.omnicore.identity.security.InvalidTokenVersionException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageResolver messageResolver;

    @ExceptionHandler({
        BadCredentialsException.class,
        UsernameNotFoundException.class
    })
    public ProblemDetail handleBadCredentials(Exception ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            messageResolver.resolve(MessageKeys.INVALID_EMAIL_OR_PASSWORD)
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabled(DisabledException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            messageResolver.resolve(MessageKeys.USER_ACCOUNT_INACTIVE)
        );
    }

    @ExceptionHandler(InvalidTokenVersionException.class)
    public ProblemDetail handleInvalidTokenVersion(InvalidTokenVersionException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            messageResolver.resolve(MessageKeys.TOKEN_VERSION_INVALID)
        );
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePermissionNotFound(PermissionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiResponse.failure(
                messageResolver.resolve(MessageKeys.PERMISSION_NOT_FOUND),
                PermissionErrorCode.PERMISSION_NOT_FOUND.name()
            )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Invalid request parameter: " + ex.getName()
        );
    }
}
