package com.negocore.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRequestDTO {

    private Long categoryId;
    @NotBlank(message = ApplicationConstants.VALIDATION_PRODUCT_NAME_NOT_BLANK)
    private String name;
    private String sku;
    @NotNull(message = ApplicationConstants.VALIDATION_COST_PRICE_NOT_NULL)
    @Min(value = 0, message = ApplicationConstants.VALIDATION_COST_PRICE_MIN)
    private Double costPrice;
    @Min(value = 1, message = ApplicationConstants.VALIDATION_SALE_PRICE_MIN)
    @NotNull(message = ApplicationConstants.VALIDATION_SALE_PRICE_NOT_NULL)
    private Double salePrice;
    @NotNull(message = ApplicationConstants.VALIDATION_STOCK_NOT_NULL)
    @Min(value = 0, message = ApplicationConstants.VALIDATION_STOCK_MIN)
    private Integer stock;
    @NotNull(message = ApplicationConstants.VALIDATION_MIN_STOCK_ALERT_NOT_NULL)
    @Min(value = 0, message = ApplicationConstants.VALIDATION_MIN_STOCK_ALERT_MIN)
    private Integer minStockAlert;

}
