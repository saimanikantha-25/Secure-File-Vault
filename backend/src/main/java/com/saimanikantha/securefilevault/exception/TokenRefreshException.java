package com.saimanikantha.securefilevault.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when validation, rotation, or reuse of a refresh token fails.
 */
public class TokenRefreshException extends ApplicationException {

    /**
     * Constructs a new exception with HTTP 401 Unauthorized status.
     *
     * @param message the detail message
     */
    public TokenRefreshException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Constructs a new exception with a specific HTTP status.
     *
     * @param message the detail message
     * @param status  the HTTP status to return to the client
     */
    public TokenRefreshException(String message, HttpStatus status) {
        super(message, status);
    }
}
