package com.omnicore.identity.common;

import com.omnicore.identity.common.constants.MessageKeys;
import com.omnicore.identity.security.InvalidTokenVersionException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
