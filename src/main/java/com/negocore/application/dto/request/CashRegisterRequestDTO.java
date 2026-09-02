package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CashRegisterRequestDTO {


    @NotNull(message = ApplicationConstants.OPENING_AMOUNT_NOT_NULL)
    @Min(value = 1, message = ApplicationConstants.OPENING_AMOUNT_MIN)
    private Double openingAmount;
    private Double expectedAmount;
    private Double closingAmount;


}
