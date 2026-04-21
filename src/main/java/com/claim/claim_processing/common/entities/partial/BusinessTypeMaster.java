package com.claim.claim_processing.common.entities.partial;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BUSINESS_TYPE_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessTypeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder = 1;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive = "Y";

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.displayOrder == null) {
            this.displayOrder = 1;
        }
        if (this.isActive == null) {
            this.isActive = "Y";
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}