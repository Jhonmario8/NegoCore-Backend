package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DebtPaymentResponse {

    DebtPayment debtPayment;
    DebtStatus status;

}
