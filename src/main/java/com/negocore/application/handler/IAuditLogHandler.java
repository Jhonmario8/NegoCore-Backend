package com.negocore.application.handler;

import com.negocore.application.dto.response.AuditLogPageResponseDTO;

import java.time.LocalDate;

public interface IAuditLogHandler {

    AuditLogPageResponseDTO findAuditLogs(
            Long businessId,
            LocalDate from,
            LocalDate to,
            String action,
            String entity,
            Long entityId,
            Integer page,
            Integer size
    );

}