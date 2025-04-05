package com.Assignment.Exception;

import com.Assignment.Dto.ErrorResponse;

import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = "Required parameter '" + ex.getParameterName() + "' is missing";
        ErrorResponse response = new ErrorResponse(
            "MISSING_PARAMETER",
            message,
            Map.of("parameter", ex.getParameterName(), "parameterType", ex.getParameterType())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field, message);
        });
        
        ErrorResponse response = new ErrorResponse(
            "VALIDATION_FAILED",
            "Request validation failed",
            Map.of("violations", errors)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}