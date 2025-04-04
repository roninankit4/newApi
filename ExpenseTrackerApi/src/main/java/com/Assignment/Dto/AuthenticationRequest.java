package com.Assignment.Dto;

public class AuthenticationRequest {
    private String email;
    private String password;

    // Constructor
    public AuthenticationRequest() {}

    // Constructor with fields
    public AuthenticationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
