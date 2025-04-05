package com.Assignment.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.Assignment.Dto.ExpenseDto;
import com.Assignment.Entity.Expense;
import com.Assignment.Entity.User;
import com.Assignment.Exception.ExpenseErrorCode;
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

    public ExpenseResponseDto createExpense(ExpenseDto dto, User user) {
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

    public ExpenseResponseDto updateExpense(Long id, ExpenseDto dto, User user) {
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

    public double getTotalExpenses(Long userId, LocalDate start, LocalDate end) {
        logger.debug("Calculating total expenses for user: {} from {} to {}", userId, start, end);
        
        if (start.isAfter(end)) {
            logger.error("Invalid date range: {} to {}", start, end);
            throw new ExpenseException(ExpenseErrorCode.INVALID_DATE_RANGE);
        }
        
        Double total = expenseRepository.sumByUserIdAndDateRange(userId, start, end);
        return Optional.ofNullable(total).orElse(0.0);
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
    
    public byte[] generateMonthlyExcelReport(Long userId, int year, int month) throws IOException {
        logger.debug("Generating monthly report for user: {}, {}-{}", userId, month, year);
        
        if (month < 1 || month > 12) {
            logger.error("Invalid month: {}", month);
            throw new ExpenseException(ExpenseErrorCode.INVALID_MONTH);
        }

        List<Object[]> results = expenseRepository.getMonthlyCategoryTotals(userId, year, month);
        if (results.isEmpty()) {
            logger.warn("No data for monthly report - User: {}, {}-{}", userId, month, year);
            throw new ExpenseException(ExpenseErrorCode.EMPTY_REPORT);
        }

        Map<String, Double> categories = new HashMap<>();
        double total = 0.0;
        for (Object[] row : results) {
            String category = (String) row[0];
            Double amount = (Double) row[1];
            categories.put(category, amount);
            total += amount;
        }
        
        logger.info("Generated monthly report for user: {}, {}-{}", userId, month, year);
        return excelReportService.generateExcelReport(categories, total, year, month);
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