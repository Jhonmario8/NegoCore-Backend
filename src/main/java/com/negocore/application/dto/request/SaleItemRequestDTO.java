package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleItemRequestDTO {

    @NotNull(message = ApplicationConstants.PRODUCT_ID_REQUIRED)
    private Long productId;

    @NotNull(message = ApplicationConstants.QUANTITY_REQUIRED)
    @Positive(message = ApplicationConstants.QUANTITY_MUST_BE_POSITIVE)
    private Integer quantity;

    @Positive(message = ApplicationConstants.UNIT_PRICE_MUST_BE_POSITIVE)
    private BigDecimal unitPrice;

}
