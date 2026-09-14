package com.negocore.application.mapper;

import com.negocore.application.dto.request.PayablePaymentRequestDTO;
import com.negocore.application.dto.response.PayableListResponseDTO;
import com.negocore.application.dto.response.PayablePaymentDTO;
import com.negocore.application.dto.response.PayablePaymentResponseDTO;
import com.negocore.domain.model.Payable;
import com.negocore.domain.model.PayablePayment;
import com.negocore.domain.model.PayablePaymentRequest;
import com.negocore.domain.model.PayablePaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IPayableMapper {

    PayablePaymentRequest toDomain(PayablePaymentRequestDTO payablePaymentRequestDTO);

    @Mapping(source = "payablePayment", target = "payment")
    PayablePaymentResponseDTO toResponseDTO(PayablePaymentResponse payablePaymentResponse);

    PayablePaymentDTO toPaymentDTO(PayablePayment payablePayment);

    PayableListResponseDTO toListResponseDTO(Payable payable);
}
