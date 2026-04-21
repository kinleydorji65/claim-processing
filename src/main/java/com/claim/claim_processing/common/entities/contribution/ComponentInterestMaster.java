package com.claim.claim_processing.common.entities.contribution;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "COMPONENT_INTEREST_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentInterestMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "BASE_COMPONENT_CODE", length = 30)
    private String baseComponentCode;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    @Builder.Default
    private String isActive = "Y";

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        if (isActive == null) {
            isActive = "Y";
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}