package com.brandshub.userservice.exception;

/**
 * Exception thrown when a user is not found.
 * 
 * @author Brands Hub Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 