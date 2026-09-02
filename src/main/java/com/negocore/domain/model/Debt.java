package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Debt {

    private Long id;
    private Long businessId;
    private Long clientId;
    private Long saleId;
    private Double totalAmount;
    private Double paidAmount;
    private DebtStatus status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
}
