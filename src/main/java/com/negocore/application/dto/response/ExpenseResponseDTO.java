package com.negocore.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseResponseDTO {

    private Long id;
    private Long businessId;
    private Long cashRegisterId;
    private String category;
    private String description;
    private BigDecimal amount;
    private LocalDateTime createdAt;

}
