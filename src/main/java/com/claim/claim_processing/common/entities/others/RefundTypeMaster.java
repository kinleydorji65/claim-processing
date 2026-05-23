package com.claim.claim_processing.common.entities.others;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "REFUND_TYPE_MASTER",
        schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundTypeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", nullable = false, length = 50)
    private String code;

    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private String isActive = "Y";

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        if (this.isActive == null) {
            this.isActive = "Y";
        }

        if (this.createdAt == null) {
            this.createdAt = now;
        }

        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}