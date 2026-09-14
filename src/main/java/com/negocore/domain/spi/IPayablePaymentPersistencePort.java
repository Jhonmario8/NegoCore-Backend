package com.negocore.domain.spi;

import com.negocore.domain.model.PayablePayment;

public interface IPayablePaymentPersistencePort {

    PayablePayment save(PayablePayment payablePayment);
}
