package com.Assignment.Controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
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
import com.Assignment.Dto.ExpenseResponseDto;
import com.Assignment.Dto.MonthlyReportDto;
import com.Assignment.Entity.Expense;
import com.Assignment.Entity.User;
import com.Assignment.Exception.ExpenseErrorCode;
import com.Assignment.Exception.ExpenseException;
import com.Assignment.Service.ExpenseService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDto>> getAllExpenses(@AuthenticationPrincipal User user) {
        try {
            List<ExpenseResponseDto> expenses = expenseService.getAllExpensesByUserId(user.getId());
            return ResponseEntity.ok(expenses);
        } catch (ExpenseException ex) {
            logger.warn("No expenses found for user: {}", user.getId());
            throw ex;
        }
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> createExpense(
            @Valid @RequestBody ExpenseDto expenseDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user) {
        
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err -> 
                errors.put(err.getField(), err.getDefaultMessage()));
            
            throw new ExpenseException(
                ExpenseErrorCode.VALIDATION_FAILED,
                Map.of("violations", errors)
            );
        }
        
        try {
            ExpenseResponseDto expense = expenseService.createExpense(expenseDto, user);
            return ResponseEntity.ok(expense);
        } catch (ExpenseException ex) {
            logger.error("Failed to create expense: {}", ex.getMessage());
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseDto expenseDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user) {
            
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err -> 
                errors.put(err.getField(), err.getDefaultMessage()));
            
            throw new ExpenseException(
                ExpenseErrorCode.VALIDATION_FAILED,
                Map.of("violations", errors)
            );
        }
        
        try {
            ExpenseResponseDto updatedExpense = expenseService.updateExpense(id, expenseDto, user);
            return ResponseEntity.ok(updatedExpense);
        } catch (ExpenseException ex) {
            logger.error("Failed to update expense {}: {}", id, ex.getMessage());
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            expenseService.deleteExpense(id, user);
            return ResponseEntity.noContent().build();
        } catch (ExpenseException ex) {
            logger.error("Failed to delete expense {}: {}", id, ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getTotalExpenses(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @AuthenticationPrincipal User user) {

        // API Contract Validation
        if (start == null) {
            throw new ExpenseException(
                ExpenseErrorCode.MISSING_PARAMETER,
                Map.of("parameter", "start", "message", "Start date is required")
            );
        }

        LocalDate effectiveEnd = (end != null) ? end : LocalDate.now();
        
        // Delegate to service (business validation happens there)
        Double total = expenseService.getTotalExpenses(user.getId(), start, effectiveEnd);
        
        // Format response
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return ResponseEntity.ok(Map.of(
            "total", total,
            "formattedTotal", df.format(total),
            "currency", "INR",
            "period", start + " to " + effectiveEnd
        ));
    }

    @GetMapping("/category-summary")
    public ResponseEntity<Map<String, Double>> getCategoryTotals(@AuthenticationPrincipal User user) {
        try {
            Map<String, Double> totals = expenseService.getCategoryTotals(user.getId());
            return ResponseEntity.ok(totals);
        } catch (ExpenseException ex) {
            logger.warn("No expenses found for category summary: {}", user.getId());
            throw ex;
        }
    }

    @GetMapping(value = "/monthly-report", 
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
 public ResponseEntity<byte[]> getMonthlyExcelReport(
         @RequestParam(required = true, name = "year") int year,
         @RequestParam(required = true, name = "month") int month,
         @AuthenticationPrincipal User user) {
     
     if (month < 1 || month > 12) {
         throw new ExpenseException(
             ExpenseErrorCode.INVALID_MONTH,
             Map.of("message", "Month must be between 1 and 12")
         );
     }
     
     try {
         byte[] excelBytes = expenseService.generateMonthlyExcelReport(user.getId(), year, month);
         String filename = String.format("expense-report-%s-%d.xlsx", 
                                   Month.of(month).name().toLowerCase(), 
                                   year);
         
         return ResponseEntity.ok()
             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
             .body(excelBytes);
     } catch (ExpenseException ex) {
         logger.error("Report generation failed for {}-{}: {}", year, month, ex.getMessage());
         throw ex;
     } catch (IOException ex) {
         logger.error("IO error during report generation", ex);
         throw new ExpenseException(
             ExpenseErrorCode.EXCEL_GENERATION_FAILED,
             Map.of("year", year, "month", month)
         );
     }
 }
}