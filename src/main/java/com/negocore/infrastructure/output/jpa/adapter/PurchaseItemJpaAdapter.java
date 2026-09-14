package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.PurchaseItem;
import com.negocore.domain.spi.IPurchaseItemsPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IPurchaseItemEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IPurchaseItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseItemJpaAdapter implements IPurchaseItemsPersistencePort {

    private final IPurchaseItemRepository repository;
    private final IPurchaseItemEntityMapper mapper;

    @Override
    public void saveAll(List<PurchaseItem> purchaseItems) {
        repository.saveAll(purchaseItems.stream().map(mapper::toEntity).toList());
    }

    @Override
    public List<PurchaseItem> findAllByPurchaseId(Long purchaseId) {
        return repository.findAllByPurchaseId(purchaseId).stream().map(mapper::toDomain).toList();
    }
}
