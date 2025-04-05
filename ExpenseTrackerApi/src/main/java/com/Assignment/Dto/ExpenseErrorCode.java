package com.Assignment.Dto;

import org.springframework.http.HttpStatus;

public enum ExpenseErrorCode {
    // Resource Not Found (404)
    EXPENSE_NOT_FOUND("Expense not found or access denied", HttpStatus.NOT_FOUND),
    NO_EXPENSES_FOUND("No expenses found for this user", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    EMPTY_REPORT("No data available for the selected period", HttpStatus.NOT_FOUND),

    // Validation Errors (400)
    INVALID_AMOUNT("Amount must be positive", HttpStatus.BAD_REQUEST),
    INVALID_DATE("Date must be provided and cannot be in the future", HttpStatus.BAD_REQUEST),
    INVALID_DATE_RANGE("Start date must be before end date", HttpStatus.BAD_REQUEST),
    INVALID_MONTH("Month must be between 1-12", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT("Invalid date format. Use YYYY-MM-DD", HttpStatus.BAD_REQUEST),
    CATEGORY_REQUIRED("Category is required and cannot be blank", HttpStatus.BAD_REQUEST),
    WEAK_PASSWORD("Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER("Required parameter is missing", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED("Validation failed for one or more fields", HttpStatus.BAD_REQUEST),

    // Authentication/Authorization (401/403/409)
    INVALID_CREDENTIALS("Invalid email/password", HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED("Account is disabled", HttpStatus.FORBIDDEN),
    EMAIL_EXISTS("Email already registered", HttpStatus.CONFLICT),

    // System Errors (500)
    EXCEL_GENERATION_FAILED("Failed to generate Excel report", HttpStatus.INTERNAL_SERVER_ERROR);

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