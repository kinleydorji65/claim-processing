package com.claim.claim_processing.application.entity.workFlow;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.statusMaster.VerificationStatusMaster;

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
    private VerificationStatusMaster verificationStatus;

    @Column(name = "REQUIRES_RECALCULATION", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum requiresRecalculation = ActivityEnum.N;

    @Column(name = "REQUIRES_MANUAL_REVIEW", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum requiresManualReview = ActivityEnum.N;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_REVIEW_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAV_MEMBER_REVIEW_STATUS"))
    private ReviewStatusMaster memberReviewStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANK_REVIEW_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAV_BANK_REVIEW_STATUS"))
    private ReviewStatusMaster bankReviewStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_REVIEW_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAV_DOC_REVIEW_STATUS"))
    private ReviewStatusMaster documentReviewStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTRIBUTION_REVIEW_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAV_CONTRIB_REVIEW_STATUS"))
    private ReviewStatusMaster contributionReviewStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_REVIEW_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAV_RULE_REVIEW_STATUS"))
    private ReviewStatusMaster ruleReviewStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAN_REVIEW_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAV_LOAN_REVIEW_STATUS"))
    private ReviewStatusMaster loanReviewStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEDUCTION_REVIEW_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAV_DEDUCTION_REVIEW_STATUS"))
    private ReviewStatusMaster deductionReviewStatus;

    @Column(name = "RETURN_REASON", length = 1000)
    private String returnReason;

    @Column(name = "REJECTION_REASON", length = 1000)
    private String rejectionReason;

    @Column(name = "VERIFIER_REMARKS", length = 2000)
    private String verifierRemarks;

    @Column(name = "VERIFIED_BY", length = 100)
    private String verifiedBy;

    @Column(name = "VERIFIED_BY_ROLE", length = 100)
    private Long verifiedByRole;

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