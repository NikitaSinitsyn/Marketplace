package com.skypro.Marketplace.exception;

/**
 * Exception thrown when an ad is not found in the system.
 */
public class AdNotFoundException extends RuntimeException {
    public AdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdNotFoundException(String message) {
        super(message);
    }
}
