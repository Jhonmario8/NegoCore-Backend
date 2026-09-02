package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CashRegister {

    private Long id;
    private Long businessId;
    private Double openingAmount;
    private Double expectedAmount;
    private Double closingAmount;
    private CashRegisterStatus status;
    private LocalDateTime openingAt;
    private LocalDateTime closingAt;

}
