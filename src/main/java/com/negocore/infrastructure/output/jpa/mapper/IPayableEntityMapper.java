package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.Payable;
import com.negocore.infrastructure.output.jpa.entity.PayableEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IPayableEntityMapper {

    Payable toDomain(PayableEntity payableEntity);

    PayableEntity toEntity(Payable payable);
}
