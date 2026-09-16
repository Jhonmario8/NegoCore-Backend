package com.negocore.infrastructure.output.jpa.entity;

import com.negocore.domain.model.PaymentMethod;
import com.negocore.domain.model.PurchaseStatus;
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
@Table(name = "purchases")
public class PurchaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "business_id", nullable = false)
    private Long businessId;
    @Column(name = "provider_id", nullable = false)
    private Long providerId;
    @Column(name = "total", nullable = false)
    private BigDecimal total;
    @Column(name = "shipping_cost")
    private BigDecimal shippingCost;
    @Column(name = "paid_amount", nullable = false)
    private BigDecimal paidAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PurchaseStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
