package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItem {

    private Long id;
    private Long purchaseId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal subtotal;
}
