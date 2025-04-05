package com.Assignment.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.Assignment.Dto.ExpenseDto;
import com.Assignment.Entity.Expense;
import com.Assignment.Entity.User;
import com.Assignment.Exception.ExpenseErrorCode;
import com.Assignment.Exception.ExpenseException;
import com.Assignment.Repository.ExpenseRepository;
import com.Assignment.Repository.UserRepository;
import com.Assignment.Dto.ExpenseResponseDto;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private ExcelReportService excelReportService;


    public List<ExpenseResponseDto> getAllExpensesByUserId(Long userId) {
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        
        if (expenses.isEmpty()) {
            throw new ExpenseException(ExpenseErrorCode.NO_EXPENSES_FOUND);
        }
    
        return expenses.stream()
                       .map(this::mapToResponseDto)
                       .toList();
    }
    

    public Expense createExpense(ExpenseDto dto, User user) {
        // 1. Validate amount
        if (dto.getAmount() <= 0) {
            throw new ExpenseException(
                ExpenseErrorCode.INVALID_AMOUNT,
                Map.of("providedAmount", dto.getAmount())
            );
        }

        // 2. Validate category
        if (dto.getCategory() == null || dto.getCategory().isBlank()) {
            throw new ExpenseException(ExpenseErrorCode.CATEGORY_REQUIRED);
        }

        // 3. Validate date (if provided)
        if (dto.getDate() != null && dto.getDate().isAfter(LocalDate.now())) {
            throw new ExpenseException(
                ExpenseErrorCode.INVALID_DATE,
                Map.of("providedDate", dto.getDate())
            );
        }

        // 4. Save expense
        Expense expense = new Expense();
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setCategory(dto.getCategory());
        expense.setDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());
        expense.setUser(user);
        
        return expenseRepository.save(expense);
    }

    public ExpenseResponseDto updateExpense(Long id, ExpenseDto dto, User user) {
        Expense existing = expenseRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ExpenseException(
                ExpenseErrorCode.EXPENSE_NOT_FOUND,
                Map.of("expenseId", id, "userId", user.getId())
            ));

        existing.setAmount(dto.getAmount());
        existing.setDescription(dto.getDescription());
        existing.setCategory(dto.getCategory());
        existing.setDate(dto.getDate());
        
        return mapToResponseDto(expenseRepository.save(existing));
    }

 // Delete expense
    public void deleteExpense(Long id, User user) {
        if (!expenseRepository.existsByIdAndUserId(id, user.getId())) {
            throw new ExpenseException(
                ExpenseErrorCode.EXPENSE_NOT_FOUND,
                Map.of("expenseId", id)
            );
        }
        expenseRepository.deleteById(id);
    }

    // Business Logic - Total expenses
    public double getTotalExpenses(Long userId, LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new ExpenseException(ExpenseErrorCode.INVALID_DATE_RANGE);
        }
        
        Double total = expenseRepository.sumByUserIdAndDateRange(userId, start, end);
        return Optional.ofNullable(total).orElse(0.0);
    }

    // Business Logic - Category totals
    public Map<String, Double> getCategoryTotals(Long userId) {
        List<Object[]> results = expenseRepository.getCategoryTotals(userId);
        if (results.isEmpty()) {
            throw new ExpenseException(ExpenseErrorCode.NO_EXPENSES_FOUND);
        }
        
        Map<String, Double> categoryMap = new HashMap<>();
        results.forEach(row -> categoryMap.put((String) row[0], (Double) row[1]));
        return categoryMap;
    }
    
    public byte[] generateMonthlyExcelReport(Long userId, int year, int month) throws IOException {
        // Validate month
    	 if (month < 1 || month > 12) {
             throw new ExpenseException(ExpenseErrorCode.INVALID_MONTH);
         }

         List<Object[]> results = expenseRepository.getMonthlyCategoryTotals(userId, year, month);
         if (results.isEmpty()) {
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
        
        return excelReportService.generateExcelReport(categories, total, year, month);
    }

    public ExpenseResponseDto mapToResponseDto(Expense expense) {
        return new ExpenseResponseDto(
            expense.getId(),
            expense.getAmount(),
            expense.getDescription(),
            expense.getCategory(),
            expense.getDate(),
            expense.getUser().getId()
        );
    }
    
}
