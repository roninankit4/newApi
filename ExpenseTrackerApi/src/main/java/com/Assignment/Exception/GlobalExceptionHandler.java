package com.Assignment.Exception;

import com.Assignment.Dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ExpenseException.class)
    public ResponseEntity<ErrorResponse> handleExpenseException(ExpenseException ex, HttpServletRequest request) {
        logError(ex);
        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().name(),
            ex.getErrorCode().getMessage(),
            request.getRequestURI(),
            ex.getDetails()
        );
        return ResponseEntity
            .status(ex.getErrorCode().getHttpStatus())
            .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        logError(ex);
        
        Map<String, Object> details = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fieldError -> fieldError.getDefaultMessage() != null ? 
                    fieldError.getDefaultMessage() : "Invalid value"
            ));

        ErrorResponse response = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed for request",
            request.getRequestURI(),
            details
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        logError(ex);
        
        Map<String, Object> details = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                violation -> violation.getMessage() != null ? 
                    violation.getMessage() : "Invalid value"
            ));

        ErrorResponse response = new ErrorResponse(
            "CONSTRAINT_VIOLATION",
            "Constraint violation in request",
            request.getRequestURI(),
            details
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        HttpMessageNotReadableException.class,
        MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(
            Exception ex, HttpServletRequest request) {
        logError(ex);
        
        ErrorResponse response = new ErrorResponse(
            "INVALID_REQUEST",
            ex.getMessage(),
            request.getRequestURI(),
            null
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception ex, HttpServletRequest request) {
        logError(ex);
        
        ErrorResponse response = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred",
            request.getRequestURI(),
            null
        );
        return ResponseEntity.internalServerError().body(response);
    }

    private void logError(Exception ex) {
        logger.error("Exception occurred: {}", ex.getMessage(), ex);
    }
}