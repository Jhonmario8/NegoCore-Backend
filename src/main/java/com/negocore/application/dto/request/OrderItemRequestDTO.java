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
public class OrderItemRequestDTO {

    @NotNull(message = ApplicationConstants.PRODUCT_ID_REQUIRED)
    private Long productId;

    @NotNull(message = ApplicationConstants.QUANTITY_REQUIRED)
    @Positive(message = ApplicationConstants.QUANTITY_MUST_BE_POSITIVE)
    private Integer quantity;

    private Long clientId;

    private String requesterName;

    @PositiveOrZero(message = ApplicationConstants.UNIT_COST_MIN)
    private BigDecimal unitCost;

    @PositiveOrZero(message = ApplicationConstants.SALE_PRICE_MIN)
    private BigDecimal salePrice;
}
