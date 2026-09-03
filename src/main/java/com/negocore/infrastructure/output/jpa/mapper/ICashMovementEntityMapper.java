package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.CashMovement;
import com.negocore.infrastructure.output.jpa.entity.CashMovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ICashMovementEntityMapper {

    CashMovement toDomain(CashMovementEntity cashMovementEntity);

    CashMovementEntity toEntity(CashMovement cashMovement);

}
