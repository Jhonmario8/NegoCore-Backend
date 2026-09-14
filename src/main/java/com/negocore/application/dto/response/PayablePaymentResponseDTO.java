package com.negocore.application.dto.response;

import com.negocore.domain.model.PayableStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PayablePaymentResponseDTO {

    private PayablePaymentDTO payment;
    private PayableStatus status;
}
