package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.Purchase;
import com.negocore.infrastructure.output.jpa.entity.PurchaseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IPurchaseEntityMapper {

    Purchase toDomain(PurchaseEntity purchaseEntity);

    PurchaseEntity toEntity(Purchase purchase);
}
