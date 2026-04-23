package com.claim.claim_processing.claim.entity.application;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.claim.claim_processing.common.entities.common.DeductionReferenceTypeMaster;
import com.claim.claim_processing.common.entities.common.DeductionTypeMaster;
import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

@Entity
@Table(name = "CLAIM_APPLICATION_DEDUCTION_DETAIL", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationDeductionDetail {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CADD_DED_CLAIM_APP"))
        private ClaimApplication claimApplication;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "DEDUCTION_TYPE_ID", foreignKey = @ForeignKey(name = "FK_CADD_DED_TYPE"))
        private DeductionTypeMaster deductionType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "DEDUCTION_REFERENCE_TYPE_ID", foreignKey = @ForeignKey(name = "FK_CADD_DED_REF_TYPE"))
        private DeductionReferenceTypeMaster deductionReferenceType;

        @Column(name = "OUTSTANDING_AMOUNT", precision = 15, scale = 2)
        private BigDecimal outstandingAmount;

        @Column(name = "SYSTEM_DEDUCTED_AMOUNT", precision = 15, scale = 2)
        private BigDecimal systemDeductedAmount;

        @Column(name = "VERIFIED_DEDUCTED_AMOUNT", precision = 15, scale = 2)
        private BigDecimal verifiedDeductedAmount;

        @Column(name = "APPROVED_DEDUCTED_AMOUNT", precision = 15, scale = 2)
        private BigDecimal approvedDeductedAmount;

        @Column(name = "DEDUCTED_AMOUNT", precision = 15, scale = 2)
        private BigDecimal deductedAmount;

        @Column(name = "PRIORITY_ORDER")
        private Integer priorityOrder;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "DEDUCTION_REVIEW_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CADD_DED_REVIEW_STATUS"))
        private ReviewStatusMaster deductionReviewStatus;

        @Column(name = "IS_AUTO_APPLIED", length = 1)
        @Builder.Default
        private String isAutoApplied = "N";

        @Column(name = "IS_MANUAL_OVERRIDE", length = 1)
        @Builder.Default
        private String isManualOverride = "N";

        @Column(name = "OVERRIDE_REASON", length = 1000)
        private String overrideReason;

        @Column(name = "REMARKS", length = 1000)
        private String remarks;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private Timestamp createdAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

        @Column(name = "UPDATED_AT", insertable = false, updatable = false)
        private Timestamp updatedAt;

        @Enumerated(EnumType.STRING)
        @Column(name = "IS_ACTIVE", length = 1)
        @Builder.Default
        private ActivityEnum isActive = ActivityEnum.Y;

        @PrePersist
        public void prePersist() {
                createdAt = new Timestamp(System.currentTimeMillis());
                updatedAt = new Timestamp(System.currentTimeMillis());
        }

        @PreUpdate
        public void preUpdate() {
                updatedAt = new Timestamp(System.currentTimeMillis());
        }

}
