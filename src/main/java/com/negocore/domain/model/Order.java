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
public class Order {

    private Long id;
    private Long businessId;
    private Integer orderNumber;
    private OrderStatus status;
    private Long convertedPurchaseId;
    private LocalDateTime createdAt;
    private LocalDateTime convertedAt;

}
