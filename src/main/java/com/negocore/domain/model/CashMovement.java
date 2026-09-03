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
public class CashMovement {

    private Long id;
    private Long cashRegisterId;
    private CashMovementType type;
    private BigDecimal amount;
    private String description;
    private Long referenceId;
    private LocalDateTime createdAt;
}
