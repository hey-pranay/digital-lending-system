package com.sprint.lending.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle resource not found
    @ExceptionHandler({
            BorrowerProfileNotFoundException.class,
            LoanNotFoundException.class,
            RepaymentNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(
            RuntimeException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    // Handle invalid operations
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class,
            LoanEligibilityException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            RuntimeException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    // Handle duplicate loan operations
    @ExceptionHandler(DuplicateLoanException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            DuplicateLoanException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // Handle request validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");

        response.put(
                "message",
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .toList()
        );

        return ResponseEntity.badRequest().body(response);
    }

    // Common response structure
    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String message) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);

        return ResponseEntity.status(status).body(response);
    }
}
