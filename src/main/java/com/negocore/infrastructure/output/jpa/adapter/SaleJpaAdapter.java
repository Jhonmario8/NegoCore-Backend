package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Sale;
import com.negocore.domain.model.SaleStatus;
import com.negocore.domain.spi.ISalePersistencePort;
import com.negocore.infrastructure.output.jpa.entity.SaleEntity;
import com.negocore.infrastructure.output.jpa.mapper.ISaleEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.ISaleRepository;
import com.negocore.infrastructure.output.jpa.specification.SaleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaleJpaAdapter implements ISalePersistencePort {

    private final ISaleRepository repository;
    private final ISaleEntityMapper mapper;

    @Override
    public Sale saveSale(Sale sale) {
        return mapper.toDomain(repository.save(mapper.toEntity(sale)));
    }

    @Override
    public Optional<Sale> findById(Long saleId) {
        return repository.findById(saleId).map(mapper::toDomain);
    }

    @Override
    public BigDecimal sumTotalByBusinessIdAndCreatedAtBetweenAndStatusNot(Long businessId, LocalDateTime from, LocalDateTime to, SaleStatus status) {
        return repository.sumTotalByBusinessIdAndCreatedAtRange(businessId, from, to, status);
    }

    @Override
    public Long countByBusinessIdAndCreatedAtBetweenAndStatusNot(Long businessId, LocalDateTime from, LocalDateTime to, SaleStatus status) {
        return repository.countByBusinessIdAndCreatedAtRange(businessId, from, to, status);
    }

    @Override
    public Optional<Sale> findByIdAndBusinessId(Long saleId, Long businessId) {
        return repository.findByIdAndBusinessId(saleId, businessId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Sale> findAllByFilters(
            Long businessId,
            SaleStatus status,
            Long clientId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        Specification<SaleEntity> specification =
                SaleSpecification.withFilters(
                        businessId,
                        status,
                        clientId,
                        from,
                        to
                );

        return repository.findAll(specification)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
