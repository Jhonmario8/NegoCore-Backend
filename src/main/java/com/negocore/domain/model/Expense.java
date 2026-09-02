package com.negocore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    private Long id;
    private Long businessId;
    private Long cashRegisterId;
    private String category;
    private String description;
    private Double amount;
    private LocalDateTime createdAt;
}
