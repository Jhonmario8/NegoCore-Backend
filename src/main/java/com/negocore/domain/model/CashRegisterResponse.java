package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CashRegisterResponse {

    private BigDecimal expectedAmount;
    private BigDecimal closingAmount;
    private BigDecimal difference;

}
