package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebtPayment {

    private Long id;
    private Long debtId;
    private Long cashRegisterId;
    private Double amount;
    private DebtPaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}
