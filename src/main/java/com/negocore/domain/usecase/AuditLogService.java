package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IAuditLogServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.AuditLogPage;
import com.negocore.domain.model.Business;
import com.negocore.domain.spi.IAuditLogsPersistencePort;
import com.negocore.domain.spi.IBusinessPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AuditLogService implements IAuditLogServicePort {

    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;

    @Override
    public AuditLogPage findAuditLogs(
            Long businessId,
            LocalDate from,
            LocalDate to,
            String action,
            String entity,
            Long entityId,
            Integer page,
            Integer size
    ) {

        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() ->
                        new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException(
                    DomainConstants.AUDIT_FROM_AFTER_TO
            );
        }

        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 50;
        }

        if (page < 0) {
            throw new BadRequestException(
                    DomainConstants.AUDIT_INVALID_PAGE
            );
        }

        if (size <= 0 || size > 100) {
            throw new BadRequestException(
                    DomainConstants.AUDIT_INVALID_SIZE
            );
        }

        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;

        if (from != null) {
            fromDateTime = from.atStartOfDay();
        }

        if (to != null) {
            toDateTime = to.plusDays(1).atStartOfDay();
        }

        return auditLogsPersistencePort.findByFilters(
                businessId,
                fromDateTime,
                toDateTime,
                action,
                entity,
                entityId,
                page,
                size
        );
    }
}