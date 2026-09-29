package com.negocore.application.dto.request;


import com.negocore.application.constants.ApplicationConstants;
import com.negocore.domain.model.DebtPaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DebtCreateRequestDTO {

    @NotNull(message = ApplicationConstants.AMOUNT_NOT_NULL)
    @Positive(message = ApplicationConstants.AMOUNT_POSITIVE)
    private BigDecimal amount;

    @NotNull(message = ApplicationConstants.PAYMENT_METHOD_REQUIRED)
    private DebtPaymentMethod paymentMethod;
}
