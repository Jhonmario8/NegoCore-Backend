package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    private Long id;
    private Long businessId;
    private String category;
    private String description;
    private BigDecimal amount;
    private Boolean paid;
    private LocalDateTime createdAt;
}
