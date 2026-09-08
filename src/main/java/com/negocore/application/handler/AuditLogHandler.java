package com.negocore.application.handler;

import com.negocore.application.dto.response.AuditLogPageResponseDTO;
import com.negocore.application.mapper.IAuditLogMapper;
import com.negocore.domain.api.IAuditLogServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuditLogHandler implements IAuditLogHandler {

    private final IAuditLogServicePort auditLogServicePort;
    private final IAuditLogMapper mapper;

    @Override
    public AuditLogPageResponseDTO findAuditLogs(
            Long businessId,
            LocalDate from,
            LocalDate to,
            String action,
            String entity,
            Long entityId,
            Integer page,
            Integer size
    ) {

        return mapper.toPageResponse(
                auditLogServicePort.findAuditLogs(
                        businessId,
                        from,
                        to,
                        action,
                        entity,
                        entityId,
                        page,
                        size
                )
        );
    }
}