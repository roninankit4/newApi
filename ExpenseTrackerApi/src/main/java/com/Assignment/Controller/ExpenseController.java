package com.Assignment.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Assignment.Dto.ExpenseDto;
import com.Assignment.Entity.Expense;
import com.Assignment.Entity.User;
import com.Assignment.Service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

	private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.getAllExpensesByUserId(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(
        @Valid @RequestBody ExpenseDto expenseDto,
        @AuthenticationPrincipal User user
    ) {
        System.out.println("Authenticated user: " + user);  
        System.out.println("User ID: " + (user != null ? user.getId() : "NULL"));  

        Expense expense = new Expense();
        expense.setAmount(expenseDto.getAmount());
        expense.setDescription(expenseDto.getDescription());
        expense.setCategory(expenseDto.getCategory());
        expense.setUser(user);  

        return ResponseEntity.ok(expenseService.createExpense(expense));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.updateExpense(id, expense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    // Business Logic Endpoints
    @GetMapping("/summary")
    public ResponseEntity<Double> getTotalExpenses(
        @RequestParam LocalDate start,
        @RequestParam LocalDate end,
        @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(expenseService.getTotalExpenses(user.getId(), start, end));
    }

    @GetMapping("/category-summary")
    public ResponseEntity<Map<String, Double>> getCategoryTotals(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.getCategoryTotals(user.getId()));
    }
}
