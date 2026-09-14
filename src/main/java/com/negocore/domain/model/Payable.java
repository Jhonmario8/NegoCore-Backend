package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payable {

    private Long id;
    private Long businessId;
    private PayeeType payeeType;
    private Long providerId;
    private String payeeName;
    private PayableSource source;
    private Long sourceId;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private PayableStatus status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
}
