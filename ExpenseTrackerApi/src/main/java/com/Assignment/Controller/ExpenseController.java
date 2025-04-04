package com.Assignment.Controller;

import jakarta.validation.Valid;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Assignment.Dto.ExpenseDto;
import com.Assignment.Entity.Expense;
import com.Assignment.Service.ExpenseService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody ExpenseDto expenseDto) {
        Expense expense = expenseService.createExpense(expenseDto);
        return ResponseEntity.ok(expense);
    }

    @GetMapping
    public ResponseEntity<Page<Expense>> getUserExpenses(
            @RequestParam Long userId,
            Pageable pageable) {
        Page<Expense> expenses = expenseService.getUserExpenses(userId, pageable);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpense(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseDto expenseDto) {
        Expense expense = expenseService.updateExpense(id, expenseDto);
        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getTotalExpensesBetweenDates(
            @RequestParam Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Double total = expenseService.getTotalExpensesBetweenDates(
                userId,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate));
        return ResponseEntity.ok(total != null ? total : 0.0);
    }
}