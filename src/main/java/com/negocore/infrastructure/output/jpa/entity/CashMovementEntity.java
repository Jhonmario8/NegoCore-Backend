package com.negocore.infrastructure.output.jpa.entity;

import com.negocore.domain.model.CashMovementType;
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
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cash_movements")
public class CashMovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cash_register_id", nullable = false)
    private Long cashRegisterId;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CashMovementType type;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "description", length = 200)
    private String description;
    @Column(name = "reference_id")
    private Long referenceId;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
