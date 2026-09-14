package com.negocore.domain.spi;

import com.negocore.domain.model.Payable;
import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;

import java.util.List;
import java.util.Optional;

public interface IPayablePersistencePort {

    Payable save(Payable payable);

    Optional<Payable> findById(Long payableId);

    List<Payable> findAllByFilters(
            Long businessId,
            PayableStatus status,
            PayeeType payeeType,
            Long providerId
    );
}
