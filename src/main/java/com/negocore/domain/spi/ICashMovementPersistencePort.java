package com.negocore.domain.spi;

import com.negocore.domain.model.CashMovement;
import com.negocore.domain.model.CashMovementType;

import java.util.List;

public interface ICashMovementPersistencePort {

    CashMovement save(CashMovement cashMovement);
    List<CashMovement> findByCashRegisterIdAndTypeIn(Long cashRegisterId, List<CashMovementType> types);
}
