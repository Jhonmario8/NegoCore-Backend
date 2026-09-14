package com.negocore.application.handler;

import com.negocore.application.dto.request.PayablePaymentRequestDTO;
import com.negocore.application.dto.response.PayableListResponseDTO;
import com.negocore.application.dto.response.PayablePaymentResponseDTO;
import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;

import java.util.List;

public interface IPayableHandler {

    PayablePaymentResponseDTO createPayablePayment(
            Long businessId,
            Long payableId,
            PayablePaymentRequestDTO payablePaymentRequestDTO
    );

    List<PayableListResponseDTO> findPayables(
            Long businessId,
            PayableStatus status,
            PayeeType payeeType,
            Long providerId
    );
}
