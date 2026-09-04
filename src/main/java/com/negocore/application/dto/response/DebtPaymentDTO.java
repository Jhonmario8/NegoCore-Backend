package com.negocore.application.dto.response;

import com.negocore.domain.model.DebtPaymentMethod;
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
public class DebtPaymentDTO {

    private Long id;
    private Long debtId;
    private Long cashRegisterId;
    private BigDecimal amount;
    private DebtPaymentMethod paymentMethod;
    private LocalDateTime createdAt;

}
