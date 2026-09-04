package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.Client;
import com.negocore.infrastructure.output.jpa.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IClientEntityMapper {

    Client toDomain(ClientEntity clientEntity);

    ClientEntity toEntity(Client client);
}
