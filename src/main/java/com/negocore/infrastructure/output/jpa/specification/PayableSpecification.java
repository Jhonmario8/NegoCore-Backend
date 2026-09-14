package com.negocore.infrastructure.output.jpa.specification;

import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;
import com.negocore.infrastructure.output.jpa.entity.PayableEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PayableSpecification {

    public static Specification<PayableEntity> withFilters(
            Long businessId,
            PayableStatus status,
            PayeeType payeeType,
            Long providerId
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

            if (payeeType != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("payeeType"), payeeType)
                );
            }

            if (providerId != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("providerId"), providerId)
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
