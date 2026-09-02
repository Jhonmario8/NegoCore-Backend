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
public class CashMovement {

    private Long id;
    private Long cashRegisterId;
    private CashMovementType type;
    private Double amount;
    private String description;
    private Long referenceId;
    private LocalDateTime createdAt;
}
