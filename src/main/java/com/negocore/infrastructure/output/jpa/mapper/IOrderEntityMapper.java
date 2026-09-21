package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.Order;
import com.negocore.infrastructure.output.jpa.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IOrderEntityMapper {

    Order toDomain(OrderEntity orderEntity);

    OrderEntity toEntity(Order order);
}
