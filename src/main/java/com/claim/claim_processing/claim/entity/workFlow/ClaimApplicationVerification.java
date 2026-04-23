package com.claim.claim_processing.claim.entity.workFlow;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.DecisionMaster;
import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.status_master.VerificationStatusMaster;

@Entity
@Table(name = "CLAIM_APPLICATION_VERIFICATION", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationVerification {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CAV_CLAIM_APP"))
        private ClaimApplication claimApplication;

        @Column(name = "VERIFICATION_LEVEL")
        private Integer verificationLevel;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "VERIFICATION_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAV_VER_STATUS"))
        private VerificationStatusMaster verificationStatus;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "VERIFICATION_DECISION_ID", foreignKey = @ForeignKey(name = "FK_CAV_VER_DECISION"))
        private DecisionMaster verificationDecision;

        @Column(name = "IS_ELIGIBLE", length = 1)
        @Builder.Default
        private String isEligible = "N";

        @Column(name = "IS_RULE_MATCHED", length = 1)
        @Builder.Default
        private String isRuleMatched = "N";

        @Column(name = "IS_DOCUMENT_VERIFIED", length = 1)
        @Builder.Default
        private String isDocumentVerified = "N";

        @Column(name = "IS_BANK_VERIFIED", length = 1)
        @Builder.Default
        private String isBankVerified = "N";

        @Column(name = "IS_CALCULATION_VERIFIED", length = 1)
        @Builder.Default
        private String isCalculationVerified = "N";

        @Column(name = "IS_DEDUCTION_CHECKED", length = 1)
        @Builder.Default
        private String isDeductionChecked = "N";

        @Column(name = "REQUIRES_RECALCULATION", length = 1)
        @Builder.Default
        private String requiresRecalculation = "N";

        @Column(name = "REQUIRES_MANUAL_REVIEW", length = 1)
        @Builder.Default
        private String requiresManualReview = "N";

        @Column(name = "RETURN_REASON", length = 1000)
        private String returnReason;

        @Column(name = "REJECTION_REASON", length = 1000)
        private String rejectionReason;

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

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "FINAL_VERIFICATION_DECISION_ID", foreignKey = @ForeignKey(name = "FK_CAV_FINAL_VER_DECISION"))
        private DecisionMaster finalVerificationDecision;

        @Column(name = "VERIFIER_REMARKS", length = 2000)
        private String verifierRemarks;

        @Column(name = "VERIFIED_BY", length = 100)
        private String verifiedBy;

        @Column(name = "VERIFIED_BY_ROLE", length = 100)
        private String verifiedByRole;

        @Column(name = "VERIFIED_AT")
        private Timestamp verifiedAt;

        @Enumerated(EnumType.STRING)
        @Column(name = "IS_ACTIVE", length = 1)
        @Builder.Default
        private ActivityEnum isActive = ActivityEnum.Y;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private Timestamp createdAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

        @Column(name = "UPDATED_AT", insertable = false, updatable = false)
        private Timestamp updatedAt;

        @PrePersist
        public void prePersist() {
                createdAt = new Timestamp(System.currentTimeMillis());
                updatedAt = new Timestamp(System.currentTimeMillis());

                if (this.isActive == null) {
                        this.isActive = ActivityEnum.Y;
                }
        }

        @PreUpdate
        public void preUpdate() {
                updatedAt = new Timestamp(System.currentTimeMillis());
        }

}
