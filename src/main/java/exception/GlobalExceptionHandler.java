package com.banking.payment.exception;

import lombok.extern.slf4j. Slf4j;
import org. springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework. validation.FieldError;
import org.springframework.web.bind. MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException. class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        log.error("Payment not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime. now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException. class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR. value(),
                "An unexpected error occurred:  " + ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Error Response DTO
    public record ErrorResponse(int status, String message, LocalDateTime timestamp) {}
}