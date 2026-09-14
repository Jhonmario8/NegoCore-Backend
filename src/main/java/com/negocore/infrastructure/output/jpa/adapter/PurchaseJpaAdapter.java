package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Purchase;
import com.negocore.domain.model.PurchaseStatus;
import com.negocore.domain.spi.IPurchasePersistencePort;
import com.negocore.infrastructure.output.jpa.entity.PurchaseEntity;
import com.negocore.infrastructure.output.jpa.mapper.IPurchaseEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IPurchaseRepository;
import com.negocore.infrastructure.output.jpa.specification.PurchaseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseJpaAdapter implements IPurchasePersistencePort {

    private final IPurchaseRepository repository;
    private final IPurchaseEntityMapper mapper;

    @Override
    public Purchase savePurchase(Purchase purchase) {
        return mapper.toDomain(repository.save(mapper.toEntity(purchase)));
    }

    @Override
    public Optional<Purchase> findByIdAndBusinessId(Long purchaseId, Long businessId) {
        return repository.findByIdAndBusinessId(purchaseId, businessId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Purchase> findAllByFilters(
            Long businessId,
            Long providerId,
            PurchaseStatus status,
            LocalDateTime from,
            LocalDateTime to
    ) {
        Specification<PurchaseEntity> specification =
                PurchaseSpecification.withFilters(
                        businessId,
                        providerId,
                        status,
                        from,
                        to
                );

        return repository.findAll(specification)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
