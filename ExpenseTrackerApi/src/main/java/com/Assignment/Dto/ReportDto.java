package com.Assignment.Dto;

import lombok.Data;
import java.util.Map;

@Data
public class ReportDto {
    private Double totalExpenses;
    private Map<String, Double> expensesByCategory;
}
