package com.negocore.application.dto.response;

import com.negocore.domain.model.PayablePaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayablePaymentDTO {

    private Long id;
    private Long payableId;
    private BigDecimal amount;
    private PayablePaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}
