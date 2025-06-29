package com.brandshub.userservice.exception;

/**
 * Exception thrown when a resource already exists (e.g., username or email).
 * 
 * @author Brands Hub Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
} 