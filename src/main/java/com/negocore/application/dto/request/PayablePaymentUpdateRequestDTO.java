package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
import com.negocore.domain.model.PayablePaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayablePaymentUpdateRequestDTO {

    @NotNull(message = ApplicationConstants.AMOUNT_NOT_NULL)
    @PositiveOrZero(message = ApplicationConstants.AMOUNT_NOT_NEGATIVE)
    private BigDecimal amount;

    @NotNull(message = ApplicationConstants.PAYMENT_METHOD_REQUIRED)
    private PayablePaymentMethod paymentMethod;
}
