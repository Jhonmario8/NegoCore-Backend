package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.OrderItem;
import com.negocore.infrastructure.output.jpa.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IOrderItemEntityMapper {

    OrderItem toDomain(OrderItemEntity orderItemEntity);

    OrderItemEntity toEntity(OrderItem orderItem);
}
