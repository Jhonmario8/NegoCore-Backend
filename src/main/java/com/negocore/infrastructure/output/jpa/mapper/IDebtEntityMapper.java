package com.negocore.infrastructure.output.jpa.mapper;


import com.negocore.domain.model.Debt;
import com.negocore.infrastructure.output.jpa.entity.DebtEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IDebtEntityMapper {

    Debt toDomain(DebtEntity debtEntity);

    DebtEntity toEntity(Debt debt);


}
