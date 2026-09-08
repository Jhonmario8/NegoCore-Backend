package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.AuditLog;
import com.negocore.infrastructure.output.jpa.entity.AuditLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IAuditLogEntityMapper {

    AuditLog toDomain(AuditLogEntity auditLogEntity);

    AuditLogEntity toEntity(AuditLog auditLog);

}
