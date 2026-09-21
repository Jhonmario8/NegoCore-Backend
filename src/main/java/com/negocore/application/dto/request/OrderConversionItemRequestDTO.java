package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
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
public class OrderConversionItemRequestDTO {

    @NotNull(message = ApplicationConstants.PRODUCT_ID_REQUIRED)
    private Long productId;

    @NotNull(message = ApplicationConstants.UNIT_COST_REQUIRED)
    @PositiveOrZero(message = ApplicationConstants.UNIT_COST_MIN)
    private BigDecimal unitCost;
}
