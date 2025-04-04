package com.Assignment.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import com.Assignment.Entity.User;

@Entity
@Table(name = "expenses")
@Data
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private double amount;
    
    private String description;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private LocalDate date = LocalDate.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}