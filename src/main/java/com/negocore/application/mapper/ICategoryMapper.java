package com.negocore.application.mapper;

import com.negocore.application.dto.response.CategoryResponseDTO;
import com.negocore.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ICategoryMapper {



    CategoryResponseDTO toResponse(Category category);
}
