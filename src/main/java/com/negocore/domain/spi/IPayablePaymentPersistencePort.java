package com.negocore.domain.spi;

import com.negocore.domain.model.PayablePayment;

import java.util.List;
import java.util.Optional;

public interface IPayablePaymentPersistencePort {

    PayablePayment save(PayablePayment payablePayment);
    List<PayablePayment> findAllByPayableId(Long payableId);
    Optional<PayablePayment> findById(Long id);
}
