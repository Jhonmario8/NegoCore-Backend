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
        name = "category",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_category_business_name",
                        columnNames = {"business_id", "name"}
                )
        }
)

public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "business_id", nullable = false)
    private Long businessId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "active", nullable = false)
    private Boolean active;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


}
