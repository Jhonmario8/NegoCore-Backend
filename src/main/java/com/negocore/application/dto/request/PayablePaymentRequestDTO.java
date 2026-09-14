package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
import com.negocore.domain.model.PayablePaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayablePaymentRequestDTO {

    @NotNull(message = ApplicationConstants.AMOUNT_NOT_NULL)
    @Positive(message = ApplicationConstants.VALIDATION_AMOUNT_POSITIVE)
    private BigDecimal amount;

    @NotNull(message = ApplicationConstants.PAYMENT_METHOD_REQUIRED)
    private PayablePaymentMethod paymentMethod;
}
