package com.negocore.infrastructure.output.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_business_sku",
                        columnNames = {"business_id", "sku"}
                )
        }
)
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long businessId;
    private Long categoryId;
    private String name;
    private String sku;
    private Double costPrice;
    private Double salePrice;
    private Integer stock;
    private Integer minStockAlert;
    private Boolean active;
    private LocalDateTime createdAt;
}
