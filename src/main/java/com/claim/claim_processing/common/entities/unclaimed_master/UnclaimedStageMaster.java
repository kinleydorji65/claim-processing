package com.claim.claim_processing.common.entities.unclaimed_master;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "UNCLAIMED_STAGE_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnclaimedStageMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", nullable = false, unique = true, length = 60)
    private String code;

    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
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
        if (this.isActive == null) {
            this.isActive = "Y";
        }
        if (this.displayOrder == null) {
            this.displayOrder = 1;
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}