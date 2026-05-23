package com.omnicore.identity.common;

import com.omnicore.identity.security.InvalidTokenVersionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        BadCredentialsException.class,
        UsernameNotFoundException.class
    })
    public ProblemDetail handleBadCredentials(Exception ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Invalid email or password"
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabled(DisabledException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "User account is inactive"
        );
    }

    @ExceptionHandler(InvalidTokenVersionException.class)
    public ProblemDetail handleInvalidTokenVersion(InvalidTokenVersionException ex) {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Token version is invalid"
        );
    }
}
