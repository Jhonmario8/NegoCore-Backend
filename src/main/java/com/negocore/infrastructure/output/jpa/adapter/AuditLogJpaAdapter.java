package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.AuditLog;
import com.negocore.domain.model.AuditLogPage;
import com.negocore.domain.spi.IAuditLogsPersistencePort;
import com.negocore.infrastructure.output.jpa.entity.AuditLogEntity;
import com.negocore.infrastructure.output.jpa.mapper.IAuditLogEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IAuditLogRepository;
import com.negocore.infrastructure.output.jpa.specification.AuditLogSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogJpaAdapter implements IAuditLogsPersistencePort {

    private final IAuditLogRepository repository;
    private final IAuditLogEntityMapper mapper;

    @Override
    public void save(AuditLog auditLog) {
        repository.save(mapper.toEntity(auditLog));
    }

    @Override
    public AuditLogPage findByFilters(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to,
            String action,
            String entity,
            Long entityId,
            int page,
            int size
    ) {

        Specification<AuditLogEntity> specification =
                AuditLogSpecification.hasBusinessId(businessId);

        if (from != null) {
            specification = specification.and(
                    AuditLogSpecification.createdAtGreaterThanOrEqualTo(from)
            );
        }

        if (to != null) {
            specification = specification.and(
                    AuditLogSpecification.createdAtLessThan(to)
            );
        }

        if (action != null) {
            specification = specification.and(
                    AuditLogSpecification.hasAction(action)
            );
        }

        if (entity != null) {
            specification = specification.and(
                    AuditLogSpecification.hasEntity(entity)
            );
        }

        if (entityId != null) {
            specification = specification.and(
                    AuditLogSpecification.hasEntityId(entityId)
            );
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<AuditLogEntity> result =
                repository.findAll(specification, pageable);

        return new AuditLogPage(
                result.getContent()
                        .stream()
                        .map(mapper::toDomain)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}