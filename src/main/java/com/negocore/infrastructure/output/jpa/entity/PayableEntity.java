package com.negocore.infrastructure.output.jpa.entity;

import com.negocore.domain.model.PayableSource;
import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;
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
@Table(name = "payables")
public class PayableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "business_id", nullable = false)
    private Long businessId;
    @Enumerated(EnumType.STRING)
    @Column(name = "payee_type", nullable = false)
    private PayeeType payeeType;
    @Column(name = "provider_id")
    private Long providerId;
    @Column(name = "payee_name", length = 150)
    private String payeeName;
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private PayableSource source;
    @Column(name = "source_id")
    private Long sourceId;
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "paid_amount", nullable = false)
    private BigDecimal paidAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PayableStatus status;
    @Column(name = "due_date")
    private LocalDate dueDate;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
