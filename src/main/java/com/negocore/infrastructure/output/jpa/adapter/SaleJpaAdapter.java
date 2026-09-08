package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Sale;
import com.negocore.domain.model.SaleStatus;
import com.negocore.domain.spi.ISalePersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.ISaleEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.ISaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
}
