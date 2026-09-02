package com.negocore.infrastructure.output.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expenses")
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "business_id", nullable = false)
    private Long businessId;
    @Column(name = "cash_register_id")
    private Long cashRegisterId;
    @Column(name = "category", length = 60)
    private String category;
    @Column(name = "description", nullable = false, length = 200)
    private String description;
    @Column(name = "amount", nullable = false)
    private Double amount;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
