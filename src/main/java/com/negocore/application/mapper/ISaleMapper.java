package com.negocore.application.mapper;

import com.negocore.application.dto.request.SaleRequestDTO;
import com.negocore.application.dto.response.SaleResponseDTO;
import com.negocore.domain.model.SaleRequest;
import com.negocore.domain.model.SaleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ISaleMapper {

    SaleResponseDTO toResponseDto(SaleResponse saleResponse);
    SaleRequest toDomain(SaleRequestDTO saleRequestDTO);
}
