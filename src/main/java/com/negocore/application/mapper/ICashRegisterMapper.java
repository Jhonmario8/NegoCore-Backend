package com.negocore.application.mapper;

import com.negocore.application.dto.request.CashRegisterOpenRequestDTO;
import com.negocore.application.dto.response.CashRegisterClosedResponseDTO;
import com.negocore.application.dto.response.CashRegisterResponseDTO;
import com.negocore.domain.model.CashRegister;
import com.negocore.domain.model.CashRegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ICashRegisterMapper {

    CashRegister toDomain(CashRegisterOpenRequestDTO cashRegisterRequestDTO);

    CashRegisterResponseDTO toResponseDTO(CashRegister cashRegister);

    CashRegisterClosedResponseDTO toClosedDto(CashRegisterResponse cashRegisterResponse);
}
