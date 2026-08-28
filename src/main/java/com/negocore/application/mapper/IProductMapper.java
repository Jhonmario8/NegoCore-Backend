package com.negocore.application.mapper;

import com.negocore.application.dto.request.ProductRequestDTO;
import com.negocore.application.dto.response.ProductResponseDTO;
import com.negocore.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IProductMapper {

    Product toDomain(ProductRequestDTO productRequestDTO);

    ProductResponseDTO toResponse(Product product);
}
