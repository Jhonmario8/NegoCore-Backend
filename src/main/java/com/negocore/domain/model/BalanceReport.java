package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceReport {

    private BigDecimal totalSales;
    private BigDecimal totalExpenses;
    private BigDecimal profit;
    private Long salesCount;

}