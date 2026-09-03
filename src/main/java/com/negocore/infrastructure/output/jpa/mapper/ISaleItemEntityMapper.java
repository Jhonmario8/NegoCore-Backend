package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.SaleItem;
import com.negocore.infrastructure.output.jpa.entity.SaleItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ISaleItemEntityMapper {

    SaleItem toDomain(SaleItemEntity saleItemEntity);

    SaleItemEntity toEntity(SaleItem saleItem);

}
