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

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "business_id")
    private Long businessId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "action", nullable = false, length = 60)
    private String action;
    @Column(name = "entity", length = 60)
    private String entity;
    @Column(name = "entity_id")
    private Long entityId;
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
