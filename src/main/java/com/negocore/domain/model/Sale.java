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
public class Sale {

    private Long id;
    private Long businessId;
    private Long cashRegisterId;
    private Long clientId;
    private Double total;
    private Double paidAmount;
    private SaleStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}
