package com.negocore.application.handler;

import com.negocore.application.dto.request.PayablePaymentRequestDTO;
import com.negocore.application.dto.response.PayableListResponseDTO;
import com.negocore.application.dto.response.PayablePaymentResponseDTO;
import com.negocore.application.mapper.IPayableMapper;
import com.negocore.domain.api.IPayableServicePort;
import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayableHandler implements IPayableHandler {

    private final IPayableMapper payableMapper;
    private final IPayableServicePort payableService;

    @Override
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
}
