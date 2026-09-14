package com.negocore.domain.spi;

import com.negocore.domain.model.PurchaseItem;

import java.util.List;

public interface IPurchaseItemsPersistencePort {

    void saveAll(List<PurchaseItem> purchaseItems);
    List<PurchaseItem> findAllByPurchaseId(Long purchaseId);
}
