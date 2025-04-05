package com.Assignment.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
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
import com.Assignment.Dto.MonthlyReportDto;
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
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.createExpense(expenseDto, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseDto expenseDto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.updateExpense(id, expenseDto, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        expenseService.deleteExpense(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<Double> getTotalExpenses(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.getTotalExpenses(user.getId(), start, end));
    }

    @GetMapping("/category-summary")
    public ResponseEntity<Map<String, Double>> getCategoryTotals(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.getCategoryTotals(user.getId()));
    }

    @GetMapping(value = "/monthly-report", 
               produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> getMonthlyExcelReport(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal User user) throws IOException {
        
        byte[] excelBytes = expenseService.generateMonthlyExcelReport(user.getId(), year, month);
        
        String filename = String.format("expense-report-%s-%d.xlsx", 
                                      Month.of(month).name().toLowerCase(), 
                                      year);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(excelBytes);
    }
}