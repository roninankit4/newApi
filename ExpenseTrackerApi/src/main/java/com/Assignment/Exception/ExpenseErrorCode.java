package com.Assignment.Exception;

import org.springframework.http.HttpStatus;

public enum ExpenseErrorCode {
    // CRUD Errors
    EXPENSE_NOT_FOUND("Expense not found or access denied", HttpStatus.NOT_FOUND),
    NO_EXPENSES_FOUND("No expenses found for this user", HttpStatus.NOT_FOUND),
    INVALID_AMOUNT("Amount must be positive", HttpStatus.BAD_REQUEST),
    
    VALIDATION_FAILED("Validation failed for one or more fields", HttpStatus.BAD_REQUEST),
    INVALID_DATE("Date must be provided and cannot be in the future", HttpStatus.BAD_REQUEST),
    CATEGORY_REQUIRED("Category is required and cannot be blank", HttpStatus.BAD_REQUEST),
    
    // Business Logic
    INVALID_DATE_RANGE("Start date must be before end date", HttpStatus.BAD_REQUEST),
    INVALID_MONTH("Month must be between 1-12", HttpStatus.BAD_REQUEST),
    EMPTY_REPORT("No data available for the selected period", HttpStatus.NOT_FOUND),
    
    // Authentication
    EMAIL_EXISTS("Email already registered", HttpStatus.CONFLICT),
    WEAK_PASSWORD("Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS("Invalid email/password", HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED("Account is disabled", HttpStatus.FORBIDDEN),
    
    // System Errors
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