package com.negocore.application.mapper;

import com.negocore.application.dto.response.AuditLogPageResponseDTO;
import com.negocore.application.dto.response.AuditLogResponseDTO;
import com.negocore.domain.model.AuditLog;
import com.negocore.domain.model.AuditLogPage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IAuditLogMapper {

    AuditLogResponseDTO toResponse(AuditLog auditLog);

    default AuditLogPageResponseDTO toPageResponse(AuditLogPage auditLogPage) {
        return new AuditLogPageResponseDTO(
                auditLogPage.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
                auditLogPage.getPage(),
                auditLogPage.getSize(),
                auditLogPage.getTotalElements(),
                auditLogPage.getTotalPages()
        );
    }

}