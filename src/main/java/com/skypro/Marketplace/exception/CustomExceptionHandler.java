package com.skypro.Marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom exception handler class to handle various exceptions and provide
 * appropriate HTTP responses with error messages.
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * Handle AdNotFoundException by returning a NOT_FOUND status and an error message.
     */
    @ExceptionHandler(AdNotFoundException.class)
    public ResponseEntity<String> handleAdNotFoundException(AdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ad not found: " + ex.getMessage());
    }

    /**
     * Handle UserAlreadyExistsException by returning a BAD_REQUEST status and an error message.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
    }

    /**
     * Handle generic Exception by returning an INTERNAL_SERVER_ERROR status and an error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }

    /**
     * Handle UserNotFoundException by returning a NOT_FOUND status and an error message.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + ex.getMessage());
    }

    /**
     * Handle IllegalArgumentException by returning a BAD_REQUEST status and an error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + ex.getMessage());
    }

    /**
     * Handle CommentNotFoundException by returning a NOT_FOUND status and an error message.
     */
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found: " + ex.getMessage());
    }

    /**
     * Handle IOException by returning an INTERNAL_SERVER_ERROR status and an error message.
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        String errorMessage = "An error occurred while processing the image: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Access forbidden");
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
}