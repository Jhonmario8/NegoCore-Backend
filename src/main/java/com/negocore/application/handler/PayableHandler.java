package com.negocore.application.handler;

import com.negocore.application.dto.request.PayablePaymentRequestDTO;
import com.negocore.application.dto.request.PayablePaymentUpdateRequestDTO;
import com.negocore.application.dto.response.PayableListResponseDTO;
import com.negocore.application.dto.response.PayablePaymentDTO;
import com.negocore.application.dto.response.PayablePaymentResponseDTO;
import com.negocore.application.mapper.IPayableMapper;
import com.negocore.domain.api.IPayableServicePort;
import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayableHandler implements IPayableHandler {

    private final IPayableMapper payableMapper;
    private final IPayableServicePort payableService;

    @Override
    @Transactional
    public PayablePaymentResponseDTO createPayablePayment(
            Long businessId,
            Long payableId,
            PayablePaymentRequestDTO payablePaymentRequestDTO
    ) {
        return payableMapper.toResponseDTO(
                payableService.createPayablePayment(
                        businessId,
                        payableId,
                        payableMapper.toDomain(payablePaymentRequestDTO)
                )
        );
    }

    @Override
    public List<PayableListResponseDTO> findPayables(
            Long businessId,
            PayableStatus status,
            PayeeType payeeType,
            Long providerId
    ) {
        return payableService.findPayables(
                        businessId,
                        status,
                        payeeType,
                        providerId
                )
                .stream()
                .map(payableMapper::toListResponseDTO)
                .toList();
    }

    @Override
    public List<PayableListResponseDTO> findGroupedPayables(Long businessId, PayableStatus status, Long providerId) {
        return payableService.findGroupedPayables(businessId, status, providerId)
                .stream()
                .map(payableMapper::toListResponseDTO)
                .toList();
    }

    @Override
    public List<PayablePaymentDTO> findPaymentsByPayable(Long businessId, Long payableId) {
        return payableService.findPaymentsByPayable(businessId, payableId)
                .stream()
                .map(payableMapper::toPaymentDTO)
                .toList();
    }

    @Override
    @Transactional
    public PayablePaymentResponseDTO updatePayablePayment(Long businessId, Long payableId, Long paymentId, PayablePaymentUpdateRequestDTO payablePaymentUpdateRequestDTO) {
        return payableMapper.toResponseDTO(
                payableService.updatePayablePayment(
                        businessId,
                        payableId,
                        paymentId,
                        payableMapper.toUpdateRequest(payablePaymentUpdateRequestDTO)
                )
        );
    }

    @Override
    @Transactional
    public List<PayablePaymentResponseDTO> payProviderPayable(Long businessId, Long providerId, PayablePaymentRequestDTO payablePaymentRequestDTO) {
        return payableService.payProviderPayable(businessId, providerId, payableMapper.toDomain(payablePaymentRequestDTO))
                .stream()
                .map(payableMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<PayablePaymentDTO> findProviderPayablePayments(Long businessId, Long providerId) {
        return payableService.findProviderPayablePayments(businessId, providerId)
                .stream()
                .map(payableMapper::toPaymentDTO)
                .toList();
    }

    @Override
    @Transactional
    public PayablePaymentResponseDTO updateProviderPayablePayment(Long businessId, Long providerId, Long paymentId, PayablePaymentUpdateRequestDTO payablePaymentUpdateRequestDTO) {
        return payableMapper.toResponseDTO(
                payableService.updateProviderPayablePayment(
                        businessId,
                        providerId,
                        paymentId,
                        payableMapper.toUpdateRequest(payablePaymentUpdateRequestDTO)
                )
        );
    }
}
