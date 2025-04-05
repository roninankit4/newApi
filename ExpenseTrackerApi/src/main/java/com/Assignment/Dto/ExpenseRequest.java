package com.Assignment.Dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ExpenseRequest {
    @Positive(message = "Amount must be positive")
    private double amount;
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "Food|Travel|Entertainment|Utilities|Other", 
             message = "Invalid category. Allowed: Food, Travel, Entertainment, Utilities, Other")
    private String category;

    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date; 
    
    public ExpenseRequest(double amount, String description, String category, LocalDate date) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }
	
	 public ExpenseRequest() {
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
    
    
}