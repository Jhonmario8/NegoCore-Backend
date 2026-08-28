package com.negocore.application.mapper;

import com.negocore.application.dto.request.BusinessCreateDTO;
import com.negocore.application.dto.response.BusinessListResponseDTO;
import com.negocore.application.dto.response.BusinessResponseDTO;
import com.negocore.domain.model.Business;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IBusinessMapper {

    Business toDomain(BusinessCreateDTO businessCreateDTO);
    BusinessResponseDTO toResponse(Business business);
    BusinessListResponseDTO toListResponse(Business business);


}
