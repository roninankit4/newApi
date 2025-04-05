package com.Assignment.Exception;

import java.util.Map;

import com.Assignment.Dto.ExpenseErrorCode;

public class ExpenseException extends RuntimeException {
    private final ExpenseErrorCode errorCode;
    private final Map<String, Object> details;

    public ExpenseException(ExpenseErrorCode errorCode) {
        this(errorCode, null);
    }

    public ExpenseException(ExpenseErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }

    // Getters
    public ExpenseErrorCode getErrorCode() { return errorCode; }
    public Map<String, Object> getDetails() { return details; }
}