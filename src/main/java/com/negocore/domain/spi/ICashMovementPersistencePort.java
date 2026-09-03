package com.negocore.domain.spi;

import com.negocore.domain.model.CashMovement;

public interface ICashMovementPersistencePort {

    CashMovement save(CashMovement cashMovement);

}
