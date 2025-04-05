package com.Assignment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Assignment.Dto.ExpenseRequest;
import com.Assignment.Dto.ExpenseResponseDto;
import com.Assignment.Entity.Expense;
import com.Assignment.Entity.User;
import com.Assignment.Exception.ExpenseException;
import com.Assignment.Repository.ExpenseRepository;
import com.Assignment.Service.ExcelReportService;
import com.Assignment.Service.ExpenseService;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExcelReportService excelReportService;

    @InjectMocks
    private ExpenseService expenseService;

    // Test data using exact constructors
    private final User testUser = new User(1L, "Test User", "test@example.com", "password", true);
    private final ExpenseRequest validDto = new ExpenseRequest(100.0, "Food", "Lunch", LocalDate.now());
    private final Expense testExpense = new Expense(1L, 100.0, "Lunch", "Food", LocalDate.now(), testUser);
    private final ExpenseResponseDto testResponseDto = new ExpenseResponseDto(1L, 100.0, "Lunch", "Food", LocalDate.now(), 1L, "ankit");
    
    
    @Test
    void getAllExpensesByUserId_shouldReturnDtos_whenExpensesExist() {
        // Arrange
        when(expenseRepository.findByUserId(1L)).thenReturn(List.of(testExpense));

        // Act
        List<ExpenseResponseDto> result = expenseService.getAllExpensesByUserId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(testResponseDto.getId(), result.get(0).getId());
        verify(expenseRepository).findByUserId(1L);
    }

    @Test
    void getAllExpensesByUserId_shouldThrow_whenNoExpensesFound() {
        // Arrange
        when(expenseRepository.findByUserId(1L)).thenReturn(List.of());

        // Act & Assert
        assertThrows(ExpenseException.class, () -> expenseService.getAllExpensesByUserId(1L));
    }
    
    @Test
    void createExpense_shouldReturnDto_whenValid() {
        // Arrange
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseResponseDto result = expenseService.createExpense(validDto, testUser);

        // Assert
        assertEquals(testResponseDto.getId(), result.getId());
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void createExpense_shouldThrow_whenInvalidAmount() {
        // Arrange
        ExpenseRequest invalidDto = new ExpenseRequest(-100.0, "Food", "Lunch", LocalDate.now());

        // Act & Assert
        assertThrows(ExpenseException.class, () -> 
            expenseService.createExpense(invalidDto, testUser));
    }

    @Test
    void createExpense_shouldUseCurrentDate_whenDateNotProvided() {
        // Arrange
        ExpenseRequest noDateDto = new ExpenseRequest(100.0, "Food", "Lunch", null);
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseResponseDto result = expenseService.createExpense(noDateDto, testUser);

        // Assert
        assertEquals(LocalDate.now(), result.getDate());
    }
    
    @Test
    void createExpense_shouldThrow_whenCategoryBlank() {
        ExpenseRequest invalidDto = new ExpenseRequest(100.0, "   ", "Desc", LocalDate.now());
        assertThrows(ExpenseException.class, () -> 
            expenseService.createExpense(invalidDto, testUser));
    }
    
    @Test
    void updateExpense_shouldReturnUpdatedDto_whenExists() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseResponseDto result = expenseService.updateExpense(1L, validDto, testUser);

        // Assert
        assertEquals(testResponseDto.getId(), result.getId());
        verify(expenseRepository).save(testExpense);
    }

    @Test
    void updateExpense_shouldThrow_whenNotFound() {
        // Arrange
        when(expenseRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ExpenseException.class, () -> 
            expenseService.updateExpense(1L, validDto, testUser));
    }
    
    @Test
    void deleteExpense_shouldDelete_whenExists() {
        // Arrange
        when(expenseRepository.existsByIdAndUserId(1L, 1L)).thenReturn(true);

        // Act
        expenseService.deleteExpense(1L, testUser);

        // Assert
        verify(expenseRepository).deleteById(1L);
    }

    @Test
    void deleteExpense_shouldThrow_whenNotFound() {
        // Arrange
        when(expenseRepository.existsByIdAndUserId(1L, 1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ExpenseException.class, () -> 
            expenseService.deleteExpense(1L, testUser));
    }
    
    @Test
    void getTotalExpenses_shouldReturnSum_whenValidRange() {
        // Arrange
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);
        when(expenseRepository.sumByUserIdAndDateRange(1L, start, end)).thenReturn(500.0);

        // Act
        double result = expenseService.getTotalExpenses(1L, start, end);

        // Assert
        assertEquals(500.0, result);
    }

    @Test
    void getTotalExpenses_shouldThrow_whenInvalidRange() {
        // Arrange
        LocalDate start = LocalDate.of(2023, 1, 31);
        LocalDate end = LocalDate.of(2023, 1, 1);

        // Act & Assert
        assertThrows(ExpenseException.class, () -> 
            expenseService.getTotalExpenses(1L, start, end));
    }
    
    @Test
    void getCategoryTotals_shouldReturnMap_whenDataExists() {
        // Arrange
        List<Object[]> mockResults = List.of(
            new Object[]{"Food", 300.0},
            new Object[]{"Transport", 200.0}
        );
        when(expenseRepository.getCategoryTotals(1L)).thenReturn(mockResults);

        // Act
        Map<String, Double> result = expenseService.getCategoryTotals(1L);

        // Assert
        assertEquals(2, result.size());
        assertEquals(300.0, result.get("Food"));
    }

    @Test
    void getCategoryTotals_shouldThrow_whenNoData() {
        // Arrange
        when(expenseRepository.getCategoryTotals(1L)).thenReturn(List.of());

        // Act & Assert
        assertThrows(ExpenseException.class, () -> expenseService.getCategoryTotals(1L));
    }
    
    @Test
    void generateMonthlyExcelReport_shouldReturnBytes_whenDataExists() throws IOException {
        // Arrange
        List<Object[]> mockResults = List.of(
            new Object[]{"Food", 300.0},
            new Object[]{"Transport", 200.0}
        );
        byte[] mockBytes = "mock excel".getBytes();
        
        when(expenseRepository.getMonthlyCategoryTotals(1L, 2023, 1)).thenReturn(mockResults);
        when(excelReportService.generateExcelReport(anyMap(), anyDouble(), anyInt(), anyInt()))
            .thenReturn(mockBytes);

        // Act
        byte[] result = expenseService.generateMonthlyExcelReport(1L, 2023, 1);

        // Assert
        assertNotNull(result);
        verify(excelReportService).generateExcelReport(anyMap(), eq(500.0), eq(2023), eq(1));
    }

    @Test
    void generateMonthlyExcelReport_shouldThrow_whenInvalidMonth() {
        // Act & Assert
        assertThrows(ExpenseException.class, () -> 
            expenseService.generateMonthlyExcelReport(1L, 2023, 13));
    }

    @Test
    void generateMonthlyExcelReport_shouldThrow_whenNoData() {
        // Arrange
        when(expenseRepository.getMonthlyCategoryTotals(1L, 2023, 1)).thenReturn(List.of());

        // Act & Assert
        assertThrows(ExpenseException.class, () -> 
            expenseService.generateMonthlyExcelReport(1L, 2023, 1));
    }
    //DTO classes testing
    private ExpenseRequest createTestDto(double amount, String category, String desc, LocalDate date) {
        ExpenseRequest dto = new ExpenseRequest();
        dto.setAmount(amount);
        dto.setCategory(category);
        dto.setDescription(desc);
        dto.setDate(date);
        return dto;
    }

    private Expense createTestExpense(Long id, double amount, String category, User user) {
        Expense expense = new Expense();
        expense.setId(id);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(LocalDate.now());
        expense.setUser(user);
        return expense;
    }
    
}