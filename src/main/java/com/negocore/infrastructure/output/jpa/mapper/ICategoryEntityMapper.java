package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.Category;
import com.negocore.infrastructure.output.jpa.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ICategoryEntityMapper {

    CategoryEntity toEntity(Category category);

    Category toDomain(CategoryEntity categoryEntity);


}
