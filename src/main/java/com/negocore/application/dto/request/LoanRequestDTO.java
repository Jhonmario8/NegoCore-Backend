package com.negocore.application.dto.request;

import com.negocore.application.constants.ApplicationConstants;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {

    @NotNull(message = ApplicationConstants.AMOUNT_NOT_NULL)
    @DecimalMin(value = "0.01", message = ApplicationConstants.AMOUNT_POSITIVE)
    private BigDecimal amount;

    private Long clientId;

    @Size(max = 100, message = ApplicationConstants.CLIENT_NAME_SIZE)
    private String debtorName;

    private LocalDate dueDate;
}
