package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.CashRegister;
import com.negocore.infrastructure.output.jpa.entity.CashRegisterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ICashRegisterEntityMapper {

    CashRegister toDomain(CashRegisterEntity cashRegisterEntity);

    CashRegisterEntity toEntity(CashRegister cashRegister);


}
