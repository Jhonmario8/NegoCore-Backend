package com.negocore.application.dto.response;

import com.negocore.domain.model.CashMovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CashMovementResponseDTO {

    private Long id;
    private Long cashRegisterId;
    private CashMovementType type;
    private BigDecimal amount;
    private String description;
    private Long referenceId;
    private LocalDateTime createdAt;

}
