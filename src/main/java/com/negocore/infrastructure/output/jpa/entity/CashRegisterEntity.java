package com.negocore.infrastructure.output.jpa.entity;

import com.negocore.domain.model.CashRegisterStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "cash_registers")
public class CashRegisterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long businessId;
    private Double openingAmount;
    private Double expectedAmount;
    private Double closingAmount;
    @Enumerated(EnumType.STRING)
    private CashRegisterStatus status;
    private LocalDateTime openingAt;
    private LocalDateTime closingAt;
}
