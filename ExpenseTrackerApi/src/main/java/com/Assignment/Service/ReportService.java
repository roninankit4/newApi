package com.Assignment.Service;

import org.springframework.stereotype.Service;

import com.Assignment.Dto.ReportDto;
import com.Assignment.Repository.ExpenseRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final ExpenseRepository expenseRepository;

    public ReportService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ReportDto generateMonthlyReport(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        Double total = expenseRepository.getTotalExpensesBetweenDates(userId, startDate, endDate);
        List<Object[]> categoryData = expenseRepository.getExpensesByCategoryBetweenDates(userId, startDate, endDate);
        
        Map<String, Double> expensesByCategory = categoryData.stream()
                .collect(Collectors.toMap(
                        e -> (String) e[0],
                        e -> (Double) e[1]
                ));
        
        ReportDto report = new ReportDto();
        report.setTotalExpenses(total != null ? total : 0.0);
        report.setExpensesByCategory(expensesByCategory);
        
        return report;
    }

    public Map<String, Double> getExpensesByCategory(Long userId) {
        List<Object[]> categoryData = expenseRepository.getExpensesByCategory(userId);
        return categoryData.stream()
                .collect(Collectors.toMap(
                        e -> (String) e[0],
                        e -> (Double) e[1]
                ));
    }
}
