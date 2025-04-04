package com.Assignment.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseDto {
    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    private Double amount;
    
    private String description;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private LocalDate date;
    
    @NotNull(message = "User ID is required")
    private Long userId;
}