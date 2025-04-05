package com.Assignment.Dto;

import java.util.Map;

public class MonthlyReportDto {
    private double total;
    private Map<String, Double> categories;
	public MonthlyReportDto() {
		
	}
	public MonthlyReportDto(double total, Map<String, Double> categories) {
		super();
		this.total = total;
		this.categories = categories;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public Map<String, Double> getCategories() {
		return categories;
	}
	public void setCategories(Map<String, Double> categories) {
		this.categories = categories;
	}

    
    
    
}