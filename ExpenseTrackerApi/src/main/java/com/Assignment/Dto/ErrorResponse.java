package com.Assignment.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final Instant timestamp;
    private final String errorCode;
    private final String message;
    private final String path;
    private final Map<String, Object> details;

    public ErrorResponse(String errorCode, String message, String path, Map<String, Object> details) {
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
        this.details = details;
    }
}