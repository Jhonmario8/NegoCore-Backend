package com.negocore.domain.spi;

import com.negocore.domain.model.Debt;

import java.util.Optional;

public interface IDebtPersistencePort {

    Debt save(Debt debt);
    Optional<Debt> findBySaleId(Long saleId);
}
