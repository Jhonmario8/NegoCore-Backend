package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.PurchaseItem;
import com.negocore.infrastructure.output.jpa.entity.PurchaseItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IPurchaseItemEntityMapper {

    PurchaseItem toDomain(PurchaseItemEntity purchaseItemEntity);

    PurchaseItemEntity toEntity(PurchaseItem purchaseItem);
}
