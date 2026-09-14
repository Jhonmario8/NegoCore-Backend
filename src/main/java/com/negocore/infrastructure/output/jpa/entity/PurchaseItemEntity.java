package com.negocore.infrastructure.output.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchase_items")
public class PurchaseItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "unit_cost", nullable = false)
    private BigDecimal unitCost;
    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;
}
