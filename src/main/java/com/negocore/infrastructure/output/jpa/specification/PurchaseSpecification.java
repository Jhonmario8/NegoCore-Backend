package com.negocore.infrastructure.output.jpa.specification;

import com.negocore.domain.model.PurchaseStatus;
import com.negocore.infrastructure.output.jpa.entity.PurchaseEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseSpecification {

    public static Specification<PurchaseEntity> withFilters(
            Long businessId,
            Long providerId,
            PurchaseStatus status,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    criteriaBuilder.equal(root.get("businessId"), businessId)
            );

            if (providerId != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("providerId"), providerId)
                );
            }

            if (status != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("status"), status)
                );
            }

            if (from != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                from
                        )
                );
            }

            if (to != null) {
                predicates.add(
                        criteriaBuilder.lessThan(
                                root.get("createdAt"),
                                to
                        )
                );
            }

            assert query != null;
            query.orderBy(
                    criteriaBuilder.desc(root.get("createdAt"))
            );

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
