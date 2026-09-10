package com.negocore.infrastructure.output.jpa.specification;

import com.negocore.domain.model.SaleStatus;
import com.negocore.infrastructure.output.jpa.entity.SaleEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class SaleSpecification {

    public static Specification<SaleEntity> withFilters(
            Long businessId,
            SaleStatus status,
            Long clientId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    criteriaBuilder.equal(root.get("businessId"), businessId)
            );

            if (status != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("status"), status)
                );
            }

            if (clientId != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("clientId"), clientId)
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