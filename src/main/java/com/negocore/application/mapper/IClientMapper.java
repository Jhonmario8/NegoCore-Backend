package com.negocore.application.mapper;

import com.negocore.application.dto.request.ClientRequestDTO;
import com.negocore.application.dto.response.ClientResponseDTO;
import com.negocore.domain.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IClientMapper {

    Client toDomain(ClientRequestDTO clientRequestDTO);

    ClientResponseDTO toResponse(Client client);


}
