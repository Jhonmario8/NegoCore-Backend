package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Sale;
import com.negocore.domain.spi.ISalePersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.ISaleEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.ISaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaleJpaAdapter implements ISalePersistencePort {

    private final ISaleRepository repository;
    private final ISaleEntityMapper mapper;

    @Override
    public Sale saveSale(Sale sale) {
        return mapper.toDomain(repository.save(mapper.toEntity(sale)));
    }
}
