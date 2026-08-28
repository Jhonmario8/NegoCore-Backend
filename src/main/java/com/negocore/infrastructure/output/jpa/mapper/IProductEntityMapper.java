package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.Product;
import com.negocore.infrastructure.output.jpa.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IProductEntityMapper {

    ProductEntity toEntity(Product product);

    Product toDomain(ProductEntity productEntity);


}
