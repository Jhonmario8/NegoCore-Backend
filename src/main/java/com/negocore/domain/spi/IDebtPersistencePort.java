package com.negocore.domain.spi;

import com.negocore.domain.model.Debt;

public interface IDebtPersistencePort {

    Debt save(Debt debt);

}
