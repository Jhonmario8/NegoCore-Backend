package com.negocore.infrastructure.output.jpa.entity;

import com.negocore.domain.model.DebtStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debts")
public class DebtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "business_id", nullable = false)
    private Long businessId;
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    @Column(name = "sale_id")
    private Long saleId;
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "paid_amount", nullable = false)
    private BigDecimal paidAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DebtStatus status;
    @Column(name = "due_date")
    private LocalDate dueDate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
