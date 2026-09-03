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
public class Product {

    private Long id;
    private Long businessId;
    private Long categoryId;
    private String name;
    private String sku;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private Integer stock;
    private Integer minStockAlert;
    private Boolean active;
    private LocalDateTime createdAt;

}
