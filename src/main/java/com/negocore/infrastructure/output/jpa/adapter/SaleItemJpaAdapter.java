package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.SaleItem;
import com.negocore.domain.spi.ISaleItemsPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.ISaleItemEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.ISaleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleItemJpaAdapter implements ISaleItemsPersistencePort {

    private final ISaleItemRepository Repository;
    private final ISaleItemEntityMapper mapper;

    @Override
    public void saveAll(List<SaleItem> saleItems) {
        Repository.saveAll(saleItems.stream().map(mapper::toEntity).toList());
    }
}
