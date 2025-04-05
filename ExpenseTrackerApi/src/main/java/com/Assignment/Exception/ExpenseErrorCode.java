package com.Assignment.Exception;

import org.springframework.http.HttpStatus;

public enum ExpenseErrorCode {
    // Validation errors (400)
    INVALID_EXPENSE_DATA("Invalid expense data provided", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("Required field is missing", HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT("Amount must be positive", HttpStatus.BAD_REQUEST),
    
    // Authentication/Authorization errors (401/403)
    UNAUTHORIZED_ACCESS("Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_OPERATION("Forbidden operation", HttpStatus.FORBIDDEN),
    
    // Not Found errors (404)
    EXPENSE_NOT_FOUND("Expense not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    
    // Conflict errors (409)
    DUPLICATE_ENTRY("Duplicate entry detected", HttpStatus.CONFLICT),
    
    // Server errors (500)
    INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    
    // Database errors (503)
    DATABASE_ERROR("Database operation failed", HttpStatus.SERVICE_UNAVAILABLE);

    private final String message;
    private final HttpStatus httpStatus;

    ExpenseErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}