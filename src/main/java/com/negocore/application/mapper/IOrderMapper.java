package com.negocore.application.mapper;

import com.negocore.application.dto.request.OrderConversionItemRequestDTO;
import com.negocore.application.dto.request.OrderConversionRequestDTO;
import com.negocore.application.dto.request.OrderItemRequestDTO;
import com.negocore.application.dto.response.OrderListResponseDTO;
import com.negocore.application.dto.response.OrderResponseDTO;
import com.negocore.domain.model.Order;
import com.negocore.domain.model.OrderConversionItemRequest;
import com.negocore.domain.model.OrderConversionRequest;
import com.negocore.domain.model.OrderItemRequest;
import com.negocore.domain.model.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IOrderMapper {

    OrderResponseDTO toResponseDto(OrderResponse orderResponse);

    OrderItemRequest toDomain(OrderItemRequestDTO orderItemRequestDTO);

    OrderConversionRequest toDomain(OrderConversionRequestDTO orderConversionRequestDTO);

    OrderConversionItemRequest toDomain(OrderConversionItemRequestDTO orderConversionItemRequestDTO);

    OrderListResponseDTO toListResponseDto(Order order);
}
