package com.skypro.Marketplace.exception;

/**
 * Exception thrown when a comment is not found in the system.
 */
public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommentNotFoundException(String message) {
        super(message);
    }
}
