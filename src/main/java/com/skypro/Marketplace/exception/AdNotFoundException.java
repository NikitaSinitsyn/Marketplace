package com.skypro.Marketplace.exception;

public class AdNotFoundException extends RuntimeException {
    public AdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdNotFoundException(String message) {
        super(message);
    }
}
