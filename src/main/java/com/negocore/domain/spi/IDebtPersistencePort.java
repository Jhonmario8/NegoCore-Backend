package com.negocore.domain.spi;

import com.negocore.domain.model.Debt;
import com.negocore.domain.model.DebtStatus;

import java.util.List;
import java.util.Optional;

public interface IDebtPersistencePort {

    Debt save(Debt debt);
    Optional<Debt> findBySaleId(Long saleId);
    Optional<Debt> findById(Long debtId);
    List<Debt> findAllByFilters(
            Long businessId,
            DebtStatus status,
            Long clientId
    );
}
