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
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "client_id")
    private Long clientId;
    @Column(name = "requester_name")
    private String requesterName;
    @Column(name = "unit_cost")
    private BigDecimal unitCost;
    @Column(name = "sale_price")
    private BigDecimal salePrice;
    @Column(name = "converted_sale_id")
    private Long convertedSaleId;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
