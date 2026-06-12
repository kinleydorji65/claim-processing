package com.claim.claim_processing.application.entity.workFlow;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(
        name = "CLAIM_APPLICATION_APPROVAL",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_APPLICATION_ID",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "FK_CAA_CLAIM_APP")
    )
    private ClaimApplication claimApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "APPROVAL_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CAA_APPROVAL_STATUS")
    )
    private StatusMaster approvalStatus;

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

    @Column(name = "REQUIRES_MANUAL_REVIEW", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum requiresManualReview = ActivityEnum.N;

    @Column(name = "APPROVER_REMARKS", length = 2000)
    private String approverRemarks;

    @Column(name = "APPROVED_BY", length = 100)
    private String approvedBy;

    @Column(name = "APPROVED_BY_ROLE", length = 100)
    private String approvedByRole;

    @Column(name = "APPROVED_AT")
    private Timestamp approvedAt;

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