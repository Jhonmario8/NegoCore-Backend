package com.negocore.domain.api;

import com.negocore.domain.model.AuditLogPage;

import java.time.LocalDate;

public interface IAuditLogServicePort {

    AuditLogPage findAuditLogs(
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