package com.Assignment.Exception;


import com.Assignment.Dto.ErrorResponse;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpenseException.class)
    public ResponseEntity<ErrorResponse> handleExpenseException(ExpenseException ex) {
        // Single handler for all your custom exceptions
        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().name(),
            ex.getErrorCode().getMessage(),
            ex.getDetails() // Already contains all validation details
        );
        return ResponseEntity
            .status(ex.getErrorCode().getHttpStatus())
            .body(response);
    }

    // Optional: Basic framework exception handling (keep minimal)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
            "INVALID_INPUT",
            "Invalid parameter value",
            Map.of(
                "parameter", ex.getName(),
                "requiredType", ex.getRequiredType().getSimpleName()
            )
        ));
    }
}