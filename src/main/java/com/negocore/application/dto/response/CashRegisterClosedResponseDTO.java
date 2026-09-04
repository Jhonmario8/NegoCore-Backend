package com.negocore.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CashRegisterClosedResponseDTO {

    private BigDecimal expectedAmount;
    private BigDecimal closingAmount;
    private BigDecimal difference;

}
