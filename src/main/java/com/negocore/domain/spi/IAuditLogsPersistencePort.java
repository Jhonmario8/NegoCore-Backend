package com.negocore.domain.spi;

import com.negocore.domain.model.AuditLog;
import com.negocore.domain.model.AuditLogPage;

import java.time.LocalDateTime;

public interface IAuditLogsPersistencePort {

    void save(AuditLog auditLog);

    AuditLogPage findByFilters(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to,
            String action,
            String entity,
            Long entityId,
            int page,
            int size
    );

}