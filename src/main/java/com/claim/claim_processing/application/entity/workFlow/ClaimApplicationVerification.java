package com.claim.claim_processing.application.entity.workFlow;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.StatusMaster;

@Entity
@Table(
        name = "CLAIM_APPLICATION_VERIFICATION",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_APPLICATION_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CAV_CLAIM_APP")
    )
    private ClaimApplication claimApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "VERIFICATION_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CAV_VER_STATUS")
    )
    private StatusMaster status;

    @Column(name = "REQUIRES_RECALCULATION", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum requiresRecalculation = ActivityEnum.N;

    @Column(name = "REQUIRES_MANUAL_REVIEW", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum requiresManualReview = ActivityEnum.N;

    @Column(name = "REJECTION_REASON", length = 1000)
    private String rejectionReason;

    @Column(name = "VERIFIER_REMARKS", length = 2000)
    private String verifierRemarks;

    @Column(name = "CLAIMED_BY", length = 100)
    private String claimedBy;

    @Column(name = "REJECTED_BY", length = 100)
    private String rejectedBy;

    @Column(name = "VERIFIED_BY", length = 100)
    private String verifiedBy;

    @Column(name = "VERIFIED_BY_ROLE_ID", length = 100)
    private Long verifiedByRoleId;

    @Column(name = "VERIFIED_AT")
    private Timestamp verifiedAt; 

    @Column(name = "IS_ACTIVE", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @PrePersist
    public void prePersist() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (requiresRecalculation == null) {
            requiresRecalculation = ActivityEnum.N;
        }

        if (requiresManualReview == null) {
            requiresManualReview = ActivityEnum.N;
        }

        if (isActive == null) {
            isActive = ActivityEnum.Y;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}