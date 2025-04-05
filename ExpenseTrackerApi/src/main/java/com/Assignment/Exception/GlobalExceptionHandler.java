package com.Assignment.Exception;


import com.Assignment.Dto.ErrorResponse;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpenseException.class)
    public ResponseEntity<ErrorResponse> handleExpenseException(ExpenseException ex) {
        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().name(),
            ex.getErrorCode().getMessage(),
            ex.getDetails()
        );
        return ResponseEntity
            .status(ex.getErrorCode().getHttpStatus())
            .body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponse response = new ErrorResponse(
            "INVALID_INPUT",
            "Invalid value for parameter: " + ex.getName(),
            null
        );
        return ResponseEntity.badRequest().body(response);
    }
}