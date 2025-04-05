package com.Assignment.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Assignment.Entity.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    // Existing methods
    List<Expense> findByUserId(Long userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end")
    Double sumByUserIdAndDateRange(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user.id = :userId GROUP BY e.category")
    List<Object[]> getCategoryTotals(@Param("userId") Long userId);
    
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId " +
           "AND YEAR(e.date) = :year " + 
           "AND MONTH(e.date) = :month " +
           "GROUP BY e.category")
    List<Object[]> getMonthlyCategoryTotals(
        @Param("userId") Long userId,
        @Param("year") int year,
        @Param("month") int month
    );
    
    // NEW METHODS ADDED
    @Query("SELECT e FROM Expense e WHERE e.id = :id AND e.user.id = :userId")
    Optional<Expense> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT COUNT(e) > 0 FROM Expense e WHERE e.id = :id AND e.user.id = :userId")
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
