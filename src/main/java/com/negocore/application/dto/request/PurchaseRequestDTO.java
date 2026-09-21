package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
import com.negocore.domain.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {

    @NotNull(message = ApplicationConstants.PROVIDER_ID_REQUIRED)
    private Long providerId;

    @NotNull(message = ApplicationConstants.PURCHASE_ITEMS_REQUIRED)
    @NotEmpty(message = ApplicationConstants.PURCHASE_ITEMS_REQUIRED)
    private List<@Valid PurchaseItemRequestDTO> purchaseItems;

    @NotNull(message = ApplicationConstants.PAYMENT_METHOD_REQUIRED)
    private PaymentMethod paymentMethod;

    @NotNull(message = ApplicationConstants.PAID_AMOUNT_REQUIRED)
    @PositiveOrZero(message = ApplicationConstants.PAID_AMOUNT_MUST_BE_POSITIVE)
    private BigDecimal paidAmount;

    @PositiveOrZero(message = ApplicationConstants.SHIPPING_COST_MUST_BE_POSITIVE)
    private BigDecimal shippingCost;

    private LocalDateTime createdAt;
}
