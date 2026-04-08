package com.arun.linkforge.Exceptions;

import com.arun.linkforge.DTO.RateLimitExceededException;
import com.arun.linkforge.DTO.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> handleRateLimit(RateLimitExceededException rateLimitExceedException){
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too Many Requests - please try again later");
    }
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Resource not found: " + ex.getMessage());
    }
}
