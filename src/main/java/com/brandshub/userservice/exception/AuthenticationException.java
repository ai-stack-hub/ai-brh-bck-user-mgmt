package com.brandshub.userservice.exception;

/**
 * Exception thrown when authentication fails.
 * 
 * @author Brands Hub Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
} 