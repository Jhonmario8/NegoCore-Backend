package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.domain.model.CashRegisterStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CashRegisterResponseDTO {

    private Long id;
    private Long businessId;
    private Double openingAmount;
    private Double expectedAmount;
    private Double closingAmount;
    private CashRegisterStatus status;
    private LocalDateTime openingAt;
    private LocalDateTime closingAt;


}
