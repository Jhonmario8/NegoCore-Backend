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
public class OrderItem {

    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Long clientId;
    private String requesterName;
    private BigDecimal unitCost;
    private BigDecimal salePrice;
    private LocalDateTime createdAt;

}
