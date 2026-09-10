package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.Provider;
import com.negocore.infrastructure.output.jpa.entity.ProviderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IProviderEntityMapper {

    Provider toDomain(ProviderEntity providerEntity);

    ProviderEntity toEntity(Provider provider);
}