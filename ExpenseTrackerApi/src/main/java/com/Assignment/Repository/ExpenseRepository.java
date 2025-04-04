package com.Assignment.Repository;

import com.Assignment.Entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Page<Expense> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate")
    List<Expense> findByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user.id = :userId GROUP BY e.category")
    List<Object[]> getExpensesByCategory(@Param("userId") Long userId);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate")
    Double getTotalExpensesBetweenDates(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate GROUP BY e.category")
    List<Object[]> getExpensesByCategoryBetweenDates(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
