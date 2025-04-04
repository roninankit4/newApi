package com.Assignment.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Assignment.Entity.Expense;
import com.Assignment.Entity.User;
import com.Assignment.Repository.ExpenseRepository;
import com.Assignment.Repository.UserRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<Expense> getAllExpensesByUserId(Long userId) {
        return expenseRepository.findByUserId(userId);
    }

    public Expense createExpense(Expense expense) {
        // Re-fetch the user to ensure it's managed by JPA
        User managedUser = userRepository.findById(expense.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        expense.setUser(managedUser);  // Re-attach
        
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {
        Expense existingExpense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setDescription(updatedExpense.getDescription());
        existingExpense.setCategory(updatedExpense.getCategory());
        existingExpense.setDate(updatedExpense.getDate());
        return expenseRepository.save(existingExpense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    // Business Logic: Total expenses in date range
    public double getTotalExpenses(Long userId, LocalDate start, LocalDate end) {
        Double total = expenseRepository.sumByUserIdAndDateRange(userId, start, end);
        return total != null ? total : 0.0;
    }

    // Business Logic: Total by category
    public Map<String, Double> getCategoryTotals(Long userId) {
        List<Object[]> results = expenseRepository.getCategoryTotals(userId);
        Map<String, Double> categoryMap = new HashMap<>();
        for (Object[] row : results) {
            categoryMap.put((String) row[0], (Double) row[1]);
        }
        return categoryMap;
    }
}
