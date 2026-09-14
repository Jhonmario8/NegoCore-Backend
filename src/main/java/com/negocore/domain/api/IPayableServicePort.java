package com.negocore.domain.api;

import com.negocore.domain.model.Payable;
import com.negocore.domain.model.PayablePaymentRequest;
import com.negocore.domain.model.PayablePaymentResponse;
import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;

import java.util.List;

public interface IPayableServicePort {

    PayablePaymentResponse createPayablePayment(
            Long businessId,
            Long payableId,
            PayablePaymentRequest payablePaymentRequest
    );

    List<Payable> findPayables(
            Long businessId,
            PayableStatus status,
            PayeeType payeeType,
            Long providerId
    );
}
