package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CashRegisterCloseRequestDTO {

    @NotNull(message = ApplicationConstants.CLOSING_AMOUNT_NOT_NULL)
    @Positive(message = ApplicationConstants.CLOSING_AMOUNT_MIN)
    private BigDecimal closingAmount;

}
