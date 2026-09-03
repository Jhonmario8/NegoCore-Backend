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
public class DebtPayment {

    private Long id;
    private Long debtId;
    private Long cashRegisterId;
    private BigDecimal amount;
    private DebtPaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}
