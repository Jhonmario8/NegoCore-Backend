package com.negocore.application.dto.response;


import com.negocore.domain.model.DebtStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DebtResponseDTO {

    DebtPaymentDTO payment;
    DebtStatus status;

}
