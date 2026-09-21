package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
import com.negocore.domain.model.PaymentMethod;
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
public class OrderItemSaleRequestDTO {

    @NotNull(message = ApplicationConstants.PAYMENT_METHOD_REQUIRED)
    private PaymentMethod paymentMethod;

    @NotNull(message = ApplicationConstants.PAID_AMOUNT_REQUIRED)
    @PositiveOrZero(message = ApplicationConstants.PAID_AMOUNT_MUST_BE_POSITIVE)
    private BigDecimal paidAmount;
}
