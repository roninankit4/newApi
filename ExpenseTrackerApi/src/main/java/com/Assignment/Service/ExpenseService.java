package com.Assignment.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Assignment.Dto.ExpenseErrorCode;
import com.Assignment.Dto.ExpenseRequest;
import com.Assignment.Entity.Expense;
import com.Assignment.Entity.User;
import com.Assignment.Exception.ExpenseException;
import com.Assignment.Repository.ExpenseRepository;
import com.Assignment.Dto.ExpenseResponseDto;

@Service
public class ExpenseService {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private ExcelReportService excelReportService;

    public List<ExpenseResponseDto> getAllExpensesByUserId(Long userId) {
        logger.debug("Fetching all expenses for user: {}", userId);
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        
        if (expenses.isEmpty()) {
            logger.warn("No expenses found for user: {}", userId);
            throw new ExpenseException(ExpenseErrorCode.NO_EXPENSES_FOUND);
        }
    
        return expenses.stream()
                     .map(this::mapToResponseDto)
                     .toList();
    }

    public ExpenseResponseDto createExpense(ExpenseRequest dto, User user) {
        logger.debug("Creating new expense for user: {}", user.getId());
        
        // Validation
        if (dto.getAmount() <= 0) {
            logger.error("Invalid amount provided: {}", dto.getAmount());
            throw new ExpenseException(
                ExpenseErrorCode.INVALID_AMOUNT,
                Map.of("providedAmount", dto.getAmount())
            );
        }

        if (dto.getCategory() == null || dto.getCategory().isBlank()) {
            logger.error("Missing category for user: {}", user.getId());
            throw new ExpenseException(ExpenseErrorCode.CATEGORY_REQUIRED);
        }

        if (dto.getDate() != null && dto.getDate().isAfter(LocalDate.now())) {
            logger.error("Future date provided: {}", dto.getDate());
            throw new ExpenseException(
                ExpenseErrorCode.INVALID_DATE,
                Map.of("providedDate", dto.getDate())
            );
        }

        Expense expense = new Expense();
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setCategory(dto.getCategory());
        expense.setDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());
        expense.setUser(user);
        
        Expense savedExpense = expenseRepository.save(expense);
        logger.info("Created new expense with ID: {}", savedExpense.getId());
        return mapToResponseDto(savedExpense);
    }

    public ExpenseResponseDto updateExpense(Long id, ExpenseRequest dto, User user) {
        logger.debug("Updating expense ID: {} for user: {}", id, user.getId());
        
        Expense existing = expenseRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> {
                logger.error("Expense not found - ID: {}, User: {}", id, user.getId());
                return new ExpenseException(
                    ExpenseErrorCode.EXPENSE_NOT_FOUND,
                    Map.of("expenseId", id, "userId", user.getId())
                );
            });

        existing.setAmount(dto.getAmount());
        existing.setDescription(dto.getDescription());
        existing.setCategory(dto.getCategory());
        existing.setDate(dto.getDate());
        
        Expense updatedExpense = expenseRepository.save(existing);
        logger.info("Updated expense ID: {}", updatedExpense.getId());
        return mapToResponseDto(updatedExpense);
    }

    public void deleteExpense(Long id, User user) {
        logger.debug("Deleting expense ID: {} for user: {}", id, user.getId());
        
        if (!expenseRepository.existsByIdAndUserId(id, user.getId())) {
            logger.error("Expense not found for deletion - ID: {}, User: {}", id, user.getId());
            throw new ExpenseException(
                ExpenseErrorCode.EXPENSE_NOT_FOUND,
                Map.of("expenseId", id)
            );
        }
        
        expenseRepository.deleteById(id);
        logger.info("Deleted expense ID: {}", id);
    }

    public Double getTotalExpenses(Long userId, LocalDate start, LocalDate end) {
        // 1. Business Logic Validation
        if (start.isAfter(end)) {
            throw new ExpenseException(
                ExpenseErrorCode.INVALID_DATE_RANGE,
                Map.of(
                    "message", "Start date must be before end date",
                    "start", start.toString(),
                    "end", end.toString()
                )
            );
        }

        // 2. Repository Call
        Double total = expenseRepository.sumByUserIdAndDateRange(userId, start, end);
        
        // 3. Result Processing
        if (total == null) { // No expenses found
            logger.info("No expenses found for user {} between {} and {}", userId, start, end);
            return 0.0;
        }

        // 4. Round to 2 decimal places for financial precision
        return BigDecimal.valueOf(total)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public Map<String, Double> getCategoryTotals(Long userId) {
        logger.debug("Calculating category totals for user: {}", userId);
        
        List<Object[]> results = expenseRepository.getCategoryTotals(userId);
        if (results.isEmpty()) {
            logger.warn("No expenses found for category totals - User: {}", userId);
            throw new ExpenseException(ExpenseErrorCode.NO_EXPENSES_FOUND);
        }
        
        Map<String, Double> categoryMap = new HashMap<>();
        results.forEach(row -> categoryMap.put((String) row[0], (Double) row[1]));
        return categoryMap;
    }
    
    public byte[] generateMonthlyExcelReport(Long userId, Integer year, Integer month) {
        // Validate null parameters
        if (year == null || month == null) {
            Map<String, String> missing = new LinkedHashMap<>();
            if (year == null) missing.put("year", "Year is required");
            if (month == null) missing.put("month", "Month is required (1-12)");
            
            throw new ExpenseException(
                ExpenseErrorCode.MISSING_PARAMETER,
                Map.of("missingParameters", missing)
            );
        }

        // Validate month range
        if (month < 1 || month > 12) {
            throw new ExpenseException(
                ExpenseErrorCode.INVALID_MONTH,
                Map.of("providedValue", month)
            );
        }

        // Get data from repository
        List<Object[]> results = expenseRepository.getMonthlyCategoryTotals(userId, year, month);
        
        // Check for empty results
        if (results.isEmpty()) {
            throw new ExpenseException(
                ExpenseErrorCode.EMPTY_REPORT,
                Map.of("year", year, "month", month)
            );
        }

        // Process data
        Map<String, Double> categories = new LinkedHashMap<>();
        double total = 0.0;
        
        for (Object[] row : results) {
            String category = (String) row[0];
            Double amount = (Double) row[1];
            categories.put(category, amount);
            total += amount;
        }

        // Generate Excel using your dedicated service
        try {
            return excelReportService.generateExcelReport(categories, total, year, month);
        } catch (IOException e) {
            throw new ExpenseException(
                ExpenseErrorCode.EXCEL_GENERATION_FAILED,
                Map.of(
                    "error", "Failed to generate Excel file",
                    "year", year,
                    "month", month
                )
            );
        }
    }

    private ExpenseResponseDto mapToResponseDto(Expense expense) {
        return new ExpenseResponseDto(
            expense.getId(),
            expense.getAmount(),
            expense.getDescription(),
            expense.getCategory(),
            expense.getDate(),
            expense.getUser().getId(),
            expense.getUser().getName()
        );
    }
}