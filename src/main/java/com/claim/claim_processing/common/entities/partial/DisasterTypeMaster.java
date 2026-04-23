package com.claim.claim_processing.common.entities.partial;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

@Entity
@Table(name = "DISASTER_TYPE_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA", uniqueConstraints = {
        @UniqueConstraint(name = "UK_DISASTER_TYPE_CODE", columnNames = "CODE")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisasterTypeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique disaster type code
     * Example: EARTHQUAKE, FLOOD, FIRE, LANDSLIDE
     */
    @Column(name = "CODE", nullable = false, length = 50, unique = true)
    private String code;

    /**
     * Display name
     */
    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    /**
     * Optional description
     */
    @Column(name = "DESCRIPTION", length = 300)
    private String description;

    /**
     * Active flag
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

    /**
     * Audit fields
     */
    @Column(name = "CREATED_AT", updatable = false)
    private Timestamp createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    /**
     * Auto-set timestamps
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = this.createdAt;
        if (this.isActive == null) {
            this.isActive = ActivityEnum.Y;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
