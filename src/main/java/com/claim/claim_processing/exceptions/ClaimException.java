package com.claim.claim_processing.exceptions;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ClaimException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final Map<String, List<String>> fieldErrors;

    // Main constructor to support message + optional cause
    private ClaimException(
            String message,
            String errorCode,
            HttpStatus httpStatus,
            Map<String, List<String>> fieldErrors,
            Throwable cause
    ) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.fieldErrors = fieldErrors;
    }

    /* =======================
       404 – Not Found
       ======================= */

    public static ClaimException notFound(String message) {
        return new ClaimException(message, "NOT_FOUND", HttpStatus.NOT_FOUND, null, null);
    }

    public static ClaimException resourceNotFound(String resource, String id) {
        return notFound(resource + " not found: " + id);
    }

    /* =======================
       403 – Forbidden
       ======================= */

    public static ClaimException accessDenied(String message) {
        return new ClaimException(message, "ACCESS_DENIED", HttpStatus.FORBIDDEN, null, null);
    }

    /* =======================
       400 – Bad Request
       ======================= */

    public static ClaimException badRequest(String message) {
        return new ClaimException(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST, null, null);
    }

    /* =======================
       400 – Validation Error
       ======================= */

    public static ClaimException validation(String message, Map<String, List<String>> fieldErrors) {
        return new ClaimException(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST, fieldErrors, null);
    }

    public static ClaimException singleValidationError(String field, String error) {
        return validation("Validation failed", Map.of(field, List.of(error)));
    }

    /* =======================
       409 – Conflict
       ======================= */

    public static ClaimException conflict(String message) {
        return new ClaimException(message, "CONFLICT", HttpStatus.CONFLICT, null, null);
    }

    /* =======================
       422 – Unprocessable Entity
       ======================= */

    public static ClaimException unprocessable(String message) {
        return new ClaimException(message, "UNPROCESSABLE_ENTITY", HttpStatus.UNPROCESSABLE_CONTENT, null, null);
    }

    /* =======================
       500 – Internal Error
       ======================= */

    public static ClaimException internalError(String message) {
        return new ClaimException(message, "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, null, null);
    }

    public static ClaimException internalError(String message, Throwable cause) {
        return new ClaimException(message, "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, null, cause);
    }
}
