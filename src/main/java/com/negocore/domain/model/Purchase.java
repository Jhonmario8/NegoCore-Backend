package com.negocore.domain.model;

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
public class Purchase {

    private Long id;
    private Long businessId;
    private Long providerId;
    private BigDecimal total;
    private BigDecimal shippingCost;
    private BigDecimal paidAmount;
    private PurchaseStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}
