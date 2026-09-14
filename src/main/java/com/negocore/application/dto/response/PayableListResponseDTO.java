package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.negocore.domain.model.PayableSource;
import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayableListResponseDTO {

    private Long id;
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
