package com.Assignment.Exception;

import java.util.Map;

import com.Assignment.Dto.ExpenseErrorCode;

public class ExpenseException extends RuntimeException {
    private final ExpenseErrorCode errorCode;
    private final Map<String, Object> details;
    private final boolean logStackTrace;

    public ExpenseException(ExpenseErrorCode errorCode) {
        this(errorCode, null, true);
    }

    public ExpenseException(ExpenseErrorCode errorCode, Map<String, Object> details) {
        this(errorCode, details, true);
    }

    public ExpenseException(ExpenseErrorCode errorCode, Map<String, Object> details, boolean logStackTrace) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
        this.logStackTrace = logStackTrace;
    }

    // Getters
    public ExpenseErrorCode getErrorCode() { return errorCode; }
    public Map<String, Object> getDetails() { return details; }
    public boolean shouldLogStackTrace() { return logStackTrace; }
}