package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.domain.model.PaymentMethod;
import com.negocore.domain.model.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleListResponseDTO {

    private Long id;
    private Long businessId;
    private Long cashRegisterId;
    private Long clientId;
    private BigDecimal total;
    private BigDecimal paidAmount;
    private SaleStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}