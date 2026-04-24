package com.claim.claim_processing.claim.entity.workFlow;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.DecisionMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.status_master.ApprovalStatusMaster;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "CLAIM_APPLICATION_APPROVAL", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CAA_CLAIM_APP"))
    private ClaimApplication claimApplication;

    @Column(name = "APPROVAL_LEVEL")
    private Integer approvalLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVAL_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAA_APPROVAL_STATUS"))
    private ApprovalStatusMaster approvalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVAL_DECISION_ID", foreignKey = @ForeignKey(name = "FK_CAA_APPROVAL_DECISION"))
    private DecisionMaster approvalDecision;

    @Column(name = "APPROVED_AMOUNT", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "APPROVED_PF_AMOUNT", precision = 15, scale = 2)
    private BigDecimal approvedPfAmount;

    @Column(name = "APPROVED_PENSION_AMOUNT", precision = 15, scale = 2)
    private BigDecimal approvedPensionAmount;

    @Column(name = "APPROVED_WITHDRAWAL_AMOUNT", precision = 15, scale = 2)
    private BigDecimal approvedWithdrawalAmount;

    @Column(name = "APPROVED_REFUND_AMOUNT", precision = 15, scale = 2)
    private BigDecimal approvedRefundAmount;

    @Column(name = "APPROVED_DEDUCTION_AMOUNT", precision = 15, scale = 2)
    private BigDecimal approvedDeductionAmount;

    @Column(name = "FINAL_NET_PAYABLE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal finalNetPayableAmount;

    @Column(name = "REQUIRES_FINANCE_ACTION", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum requiresFinanceAction = ActivityEnum.N;

    @Column(name = "REQUIRES_MANUAL_REVIEW", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum requiresManualReview = ActivityEnum.N;

    @Column(name = "APPROVAL_REASON_CODE", length = 100)
    private String approvalReasonCode;

    @Column(name = "RETURNED_REASON_CODE", length = 100)
    private String returnedReasonCode;

    @Column(name = "REJECTED_REASON_CODE", length = 100)
    private String rejectedReasonCode;

    @Column(name = "APPROVER_REMARKS", length = 2000)
    private String approverRemarks;

    @Column(name = "APPROVED_BY", length = 100)
    private String approvedBy;

    @Column(name = "APPROVED_BY_ROLE", length = 100)
    private String approvedByRole;

    @Column(name = "APPROVED_AT")
    private Timestamp approvedAt;

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

        if (this.requiresFinanceAction == null) {
            this.requiresFinanceAction = ActivityEnum.N;
        }
        if (this.requiresManualReview == null) {
            this.requiresManualReview = ActivityEnum.N;
        }
        if (this.isActive == null) {
            this.isActive = ActivityEnum.Y;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
