package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDTO {

    private Long id;
    private Long businessId;
    private Long categoryId;
    private String name;
    private String sku;
    private Double costPrice;
    private Double salePrice;
    private Integer stock;
    private Integer minStockAlert;
    private Boolean active;
    private LocalDateTime createdAt;
}
