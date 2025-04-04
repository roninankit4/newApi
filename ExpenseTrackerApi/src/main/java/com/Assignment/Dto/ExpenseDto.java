package com.Assignment.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ExpenseDto {
    @Positive(message = "Amount must be positive")
    private double amount;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;
    
    public ExpenseDto(double amount, String description, String category) {
        this.amount = amount;
        this.description = description;
        this.category = category;
    }
	
	 public ExpenseDto() {
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

    // Getters, Setters, Constructors...
    
    
}