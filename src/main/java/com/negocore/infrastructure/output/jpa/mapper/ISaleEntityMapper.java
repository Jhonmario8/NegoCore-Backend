package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.Sale;
import com.negocore.infrastructure.output.jpa.entity.SaleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ISaleEntityMapper {

    Sale toDomain(SaleEntity saleEntity);

    SaleEntity toEntity(Sale sale);

}
