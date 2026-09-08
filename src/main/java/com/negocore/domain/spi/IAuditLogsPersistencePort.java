package com.negocore.domain.spi;

import com.negocore.domain.model.AuditLog;

public interface IAuditLogsPersistencePort {

    void save(AuditLog auditLog);

}
