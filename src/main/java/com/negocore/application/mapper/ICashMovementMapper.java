package com.negocore.application.mapper;

import com.negocore.application.dto.response.CashMovementResponseDTO;
import com.negocore.domain.model.CashMovement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ICashMovementMapper {

    CashMovementResponseDTO toResponseDTO(CashMovement cashMovement);

}
