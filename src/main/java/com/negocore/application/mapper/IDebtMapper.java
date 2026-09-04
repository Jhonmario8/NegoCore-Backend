package com.negocore.application.mapper;

import com.negocore.application.dto.request.DebtCreateRequestDTO;
import com.negocore.application.dto.response.DebtPaymentDTO;
import com.negocore.application.dto.response.DebtResponseDTO;
import com.negocore.domain.model.DebtCreateRequest;
import com.negocore.domain.model.DebtPayment;
import com.negocore.domain.model.DebtPaymentResponse;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IDebtMapper {

    DebtCreateRequest toCreateRequest(DebtCreateRequestDTO debtCreateRequestDTO);

    @Mapping(source = "debtPayment", target = "payment")
    DebtResponseDTO toResponseDTO(DebtPaymentResponse debtPaymentResponse);

    DebtPaymentDTO toPaymentDTO(DebtPayment debtPayment);
}
