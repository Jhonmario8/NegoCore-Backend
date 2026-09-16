package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class QuoteItemRequestDTO {

    @NotNull(message = ApplicationConstants.PRODUCT_ID_REQUIRED)
    private Long productId;

    @NotNull(message = ApplicationConstants.QUANTITY_REQUIRED)
    @Positive(message = ApplicationConstants.QUANTITY_MUST_BE_POSITIVE)
    private Integer quantity;

    @PositiveOrZero(message = ApplicationConstants.UNIT_PRICE_MIN)
    private BigDecimal unitPrice;
}
