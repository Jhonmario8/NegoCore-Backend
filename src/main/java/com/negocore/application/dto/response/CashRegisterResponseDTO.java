package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.domain.model.CashRegisterStatus;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CashRegisterResponseDTO {

    private Long id;
    private Long businessId;
    private BigDecimal openingAmount;
    private BigDecimal expectedAmount;
    private BigDecimal closingAmount;
    private CashRegisterStatus status;
    private LocalDateTime openingAt;
    private LocalDateTime closingAt;


}
