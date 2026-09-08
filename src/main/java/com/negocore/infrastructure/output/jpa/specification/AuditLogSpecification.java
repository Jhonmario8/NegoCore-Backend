package com.negocore.infrastructure.output.jpa.specification;

import com.negocore.infrastructure.output.jpa.entity.AuditLogEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class AuditLogSpecification {

    private AuditLogSpecification() {
    }

    public static Specification<AuditLogEntity> hasBusinessId(Long businessId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("businessId"), businessId);
    }

    public static Specification<AuditLogEntity> createdAtGreaterThanOrEqualTo(
            LocalDateTime from
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        from
                );
    }

    public static Specification<AuditLogEntity> createdAtLessThan(
            LocalDateTime to
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(
                        root.get("createdAt"),
                        to
                );
    }

    public static Specification<AuditLogEntity> hasAction(String action) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("action"), action);
    }

    public static Specification<AuditLogEntity> hasEntity(String entity) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("entity"), entity);
    }

    public static Specification<AuditLogEntity> hasEntityId(Long entityId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("entityId"), entityId);
    }

}