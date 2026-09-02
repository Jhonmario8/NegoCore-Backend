package com.negocore.application.mapper;

import com.negocore.application.dto.request.CashRegisterRequestDTO;
import com.negocore.application.dto.response.CashRegisterResponseDTO;
import com.negocore.domain.model.CashRegister;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ICashRegisterMapper {

    CashRegister toDomain(CashRegisterRequestDTO cashRegisterRequestDTO);

    CashRegisterResponseDTO toResponseDTO(CashRegister cashRegister);

}
