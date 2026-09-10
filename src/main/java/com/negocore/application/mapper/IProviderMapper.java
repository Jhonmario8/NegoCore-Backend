package com.negocore.application.mapper;

import com.negocore.application.dto.request.ProviderCreateRequestDTO;
import com.negocore.application.dto.response.ProviderResponseDTO;
import com.negocore.domain.model.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IProviderMapper {

    Provider toDomain(ProviderCreateRequestDTO providerCreateRequestDTO);

    ProviderResponseDTO toResponseDTO(Provider provider);
}