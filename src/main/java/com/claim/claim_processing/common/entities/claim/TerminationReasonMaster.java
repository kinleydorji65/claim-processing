package com.claim.claim_processing.common.entities.claim;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "TERMINATION_REASON_MASTER",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_TERMINATION_REASON_CODE", columnNames = "CODE")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminationReasonMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CODE", nullable = false, length = 50)
    private String code;

    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    @Column(name = "DESCRIPTION", length = 300)
    private String description;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private Character isActive = 'Y';

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = 'Y';
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}