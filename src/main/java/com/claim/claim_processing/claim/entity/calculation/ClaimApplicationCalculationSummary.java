package com.claim.claim_processing.claim.entity.calculation;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.calculation_master.CalculationTriggerTypeMaster;
import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.status_master.CalculationStatusMaster;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(
        name = "CLAIM_APPLICATION_CALCULATION_SUMMARY",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationCalculationSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_APPLICATION_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CACS_CLAIM_APP")
    )
    private ClaimApplication claimApplication;

    @Column(name = "CALCULATION_REFERENCE_NUMBER", length = 100)
    private String calculationReferenceNumber;

    @Column(name = "CALCULATION_RUN_NO")
    private Integer calculationRunNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CALCULATION_STAGE_ID",
            foreignKey = @ForeignKey(name = "FK_CACS_CALC_STAGE")
    )
    private StageMaster calculationStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CALCULATION_TRIGGER_TYPE_ID",
            foreignKey = @ForeignKey(name = "FK_CACS_TRIGGER_TYPE")
    )
    private CalculationTriggerTypeMaster calculationTriggerType;

    @Column(name = "PRIMARY_RULE_MASTER_ID")
    private Long primaryRuleMasterId;

    @Column(name = "CALCULATION_METHOD_CODE", length = 50)
    private String calculationMethodCode;

    @Column(name = "CREDIT_METHOD_CODE", length = 50)
    private String creditMethodCode;

    @Column(name = "ROR_DECLARATION_ID")
    private Long rorDeclarationId;

    @Column(name = "ARR_RULE_ID")
    private Long arrRuleId;

    @Column(name = "IS_PENSION_BALANCE_INCLUDED", length = 1)
    @Builder.Default
    private String isPensionBalanceIncluded = "N";

    @Column(name = "CALCULATION_EFFECTIVE_DATE")
    private LocalDate calculationEffectiveDate;

    @Column(name = "BASE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "GROSS_AMOUNT", precision = 15, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "DEDUCTION_AMOUNT", precision = 15, scale = 2)
    private BigDecimal deductionAmount;

    @Column(name = "LOAN_ADJUSTMENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal loanAdjustmentAmount;

    @Column(name = "REFUND_AMOUNT", precision = 15, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "RECOVERY_AMOUNT", precision = 15, scale = 2)
    private BigDecimal recoveryAmount;

    @Column(name = "NET_PAYABLE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal netPayableAmount;

    @Column(name = "SYSTEM_CALCULATED_AMOUNT", precision = 15, scale = 2)
    private BigDecimal systemCalculatedAmount;

    @Column(name = "VERIFIED_AMOUNT", precision = 15, scale = 2)
    private BigDecimal verifiedAmount;

    @Column(name = "APPROVED_AMOUNT", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "PAID_AMOUNT", precision = 15, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "POSTED_AMOUNT", precision = 15, scale = 2)
    private BigDecimal postedAmount;

    @Column(name = "DIFFERENCE_REASON", length = 1000)
    private String differenceReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "FINAL_PAYABLE_REVIEW_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CACS_FINAL_PAYABLE_REVIEW")
    )
    private ReviewStatusMaster finalPayableReviewStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CALCULATION_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CACS_CALC_STATUS")
    )
    private CalculationStatusMaster calculationStatus;

    @Column(name = "CALCULATION_REMARKS", length = 2000)
    private String calculationRemarks;

    @Column(name = "CURRENCY_CODE", length = 10)
    private String currencyCode;

    @Column(name = "CALCULATED_BY", length = 100)
    private String calculatedBy;

    @Column(name = "CALCULATED_AT")
    private Timestamp calculatedAt;

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
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

}