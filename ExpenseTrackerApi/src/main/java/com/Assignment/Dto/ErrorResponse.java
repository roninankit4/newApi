package com.Assignment.Dto;

import java.time.Instant;
import java.util.Map;

public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, Object> details;
    private Instant timestamp = Instant.now();

    // Constructor, Getters, Setters
    public ErrorResponse(String code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

	public ErrorResponse() {
		// TODO Auto-generated constructor stub
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getDetails() {
		return details;
	}

	public void setDetails(Map<String, Object> details) {
		this.details = details;
	}
    
	public Instant getTimestamp() {
        return timestamp;
    }
	
	public void setTimestamp(Instant timestamp) { 
		this.timestamp = timestamp; 
	}
}