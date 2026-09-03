package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
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
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleRequestDTO {

    @NotNull(message = ApplicationConstants.SALE_ITEMS_REQUIRED)
    @NotEmpty(message = ApplicationConstants.SALE_ITEMS_REQUIRED)
    private List<@Valid SaleItemRequestDTO> saleItems;
    @NotNull(message = ApplicationConstants.PAYMENT_METHOD_REQUIRED)
    private PaymentMethod paymentMethod;
    @NotNull(message = ApplicationConstants.PAID_AMOUNT_REQUIRED)
    @PositiveOrZero(message = ApplicationConstants.PAID_AMOUNT_MUST_BE_POSITIVE)
    private BigDecimal paidAmount;
    private Long clientId;

}
