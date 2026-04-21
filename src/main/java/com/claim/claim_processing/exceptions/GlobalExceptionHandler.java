package com.claim.claim_processing.exceptions;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClaimException.class)
    public ResponseEntity<Map<String, Object>> handleRegistrationException(
            ClaimException ex,
            HttpServletRequest request
    ) {
        log.warn("ClaimException | status={} | code={} | message={}",
                ex.getHttpStatus(), ex.getErrorCode(), ex.getMessage());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", ex.getHttpStatus().value());
        response.put("error", ex.getHttpStatus().getReasonPhrase());
        response.put("message", ex.getMessage());
        response.put("errorCode", ex.getErrorCode());
        response.put("path", request.getRequestURI());

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = (error instanceof FieldError fe) ? fe.getField() : "unknown";
            String errorMessage = error.getDefaultMessage();
            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("error", "Bad Request");
        response.put("message", "Validation failed");
        response.put("errorCode", "VALIDATION_ERROR");
        response.put("errors", errors);
        response.put("path", request.getRequestURI());

        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.warn("Invalid JSON request for {}: {}", request.getRequestURI(), ex.getMessage());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("error", "Bad Request");
        response.put("message", "Invalid JSON format");
        response.put("errorCode", "INVALID_JSON");
        response.put("path", request.getRequestURI());

        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        log.warn("Type mismatch for {}: {}", ex.getName(), ex.getValue());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("error", "Bad Request");
        response.put("message", String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName()));
        response.put("errorCode", "TYPE_MISMATCH");
        response.put("path", request.getRequestURI());

        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 500);
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred");
        response.put("errorCode", "INTERNAL_ERROR");
        response.put("path", request.getRequestURI());

        return ResponseEntity.status(500).body(response);
    }
}

