package com.negocore.application.handler;

import com.negocore.application.dto.request.PayablePaymentRequestDTO;
import com.negocore.application.dto.response.PayableListResponseDTO;
import com.negocore.application.dto.response.PayablePaymentDTO;
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

    List<PayableListResponseDTO> findGroupedPayables(Long businessId, PayableStatus status, Long providerId);

    List<PayablePaymentDTO> findPaymentsByPayable(Long businessId, Long payableId);

    PayablePaymentResponseDTO updatePayablePayment(Long businessId, Long payableId, Long paymentId, PayablePaymentRequestDTO payablePaymentRequestDTO);

    List<PayablePaymentResponseDTO> payProviderPayable(Long businessId, Long providerId, PayablePaymentRequestDTO payablePaymentRequestDTO);

    List<PayablePaymentDTO> findProviderPayablePayments(Long businessId, Long providerId);

    PayablePaymentResponseDTO updateProviderPayablePayment(Long businessId, Long providerId, Long paymentId, PayablePaymentRequestDTO payablePaymentRequestDTO);
}
