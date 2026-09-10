package com.negocore.application.dto.response;

import com.negocore.domain.model.DebtStatus;
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
public class DebtListResponseDTO {

    private Long id;
    private Long clientId;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private DebtStatus status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
}