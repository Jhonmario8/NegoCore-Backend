package com.negocore.infrastructure.output.jpa.specification;

import com.negocore.domain.model.DebtStatus;
import com.negocore.infrastructure.output.jpa.entity.DebtEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class DebtSpecification {

    public static Specification<DebtEntity> withFilters(
            Long businessId,
            DebtStatus status,
            Long clientId
    ) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    criteriaBuilder.equal(
                            root.get("businessId"),
                            businessId
                    )
            );

            if (status != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                status
                        )
                );
            }

            if (clientId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("clientId"),
                                clientId
                        )
                );
            }

            assert query != null;
            query.orderBy(
                    criteriaBuilder.desc(
                            root.get("createdAt")
                    )
            );

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}