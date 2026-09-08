package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.AuditLog;
import com.negocore.domain.spi.IAuditLogsPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IAuditLogEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogJpaAdapter implements IAuditLogsPersistencePort {

    private final IAuditLogRepository repository;
    private final IAuditLogEntityMapper mapper;

    @Override
    public void save(AuditLog auditLog) {
        repository.save(mapper.toEntity(auditLog));
    }
}
