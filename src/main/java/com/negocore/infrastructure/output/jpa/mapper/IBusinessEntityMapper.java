package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.Business;
import com.negocore.infrastructure.output.jpa.entity.BusinessEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IBusinessEntityMapper {

    BusinessEntity toEntity(Business business);

    Business toDomain(BusinessEntity businessEntity);
}
