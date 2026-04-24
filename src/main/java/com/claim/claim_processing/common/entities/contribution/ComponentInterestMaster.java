package com.claim.claim_processing.common.entities.contribution;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "COMPONENT_INTEREST_MASTER",
        schema = "PPFMS_CONTRIBUTION_PAYMENTS_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_COMPONENT_INTEREST_CODE",
                        columnNames = {"CODE"}
                )
        }
)
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

    // -------------------------------
    // BASIC INFO
    // -------------------------------
    @Column(name = "CODE", nullable = false, length = 30)
    private String code;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    // -------------------------------
    // FK → COMPONENT_MASTER
    // -------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "BASE_COMPONENT_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_COMPONENT_INTEREST_BASE")
    )
    private ComponentMaster baseComponent;

    // -------------------------------
    // STATUS
    // -------------------------------
    @Enumerated(EnumType.STRING)
    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

    // -------------------------------
    // AUDIT
    // -------------------------------
    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    // -------------------------------
    // LIFECYCLE
    // -------------------------------
    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = ActivityEnum.Y;
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}