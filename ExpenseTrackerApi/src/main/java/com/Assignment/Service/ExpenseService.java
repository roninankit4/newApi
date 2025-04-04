package com.Assignment.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Assignment.Dto.ExpenseDto;
import com.Assignment.Entity.Expense;
import com.Assignment.Entity.User;
import com.Assignment.Exception.ResourceNotFoundException;
import com.Assignment.Repository.ExpenseRepository;

import java.time.LocalDate;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    public ExpenseService(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

    public Expense createExpense(ExpenseDto expenseDto) {
        User user = userService.getUserById(expenseDto.getUserId());
        
        Expense expense = new Expense();
        expense.setAmount(expenseDto.getAmount());
        expense.setDescription(expenseDto.getDescription());
        expense.setCategory(expenseDto.getCategory());
        expense.setDate(expenseDto.getDate() != null ? expenseDto.getDate() : LocalDate.now());
        expense.setUser(user);
        
        return expenseRepository.save(expense);
    }

    public Page<Expense> getUserExpenses(Long userId, Pageable pageable) {
        return expenseRepository.findByUserId(userId, pageable);
    }

    public Expense getExpenseById(Long expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
    }

    public Expense updateExpense(Long expenseId, ExpenseDto expenseDto) {
        Expense expense = getExpenseById(expenseId);
        User user = userService.getUserById(expenseDto.getUserId());
        
        expense.setAmount(expenseDto.getAmount());
        expense.setDescription(expenseDto.getDescription());
        expense.setCategory(expenseDto.getCategory());
        expense.setDate(expenseDto.getDate() != null ? expenseDto.getDate() : LocalDate.now());
        expense.setUser(user);
        
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long expenseId) {
        Expense expense = getExpenseById(expenseId);
        expenseRepository.delete(expense);
    }

    public Double getTotalExpensesBetweenDates(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getTotalExpensesBetweenDates(userId, startDate, endDate);
    }
}
