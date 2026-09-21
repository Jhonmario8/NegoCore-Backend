package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderListResponseDTO {

    private Long id;
    private Long businessId;
    private Integer orderNumber;
    private OrderStatus status;
    private Long convertedPurchaseId;
    private LocalDateTime createdAt;
    private LocalDateTime convertedAt;
}
