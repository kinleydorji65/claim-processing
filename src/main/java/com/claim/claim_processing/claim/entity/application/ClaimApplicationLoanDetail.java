package com.claim.claim_processing.claim.entity.application;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.common.entities.common.DecisionMaster;
import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.loan_master.LoanStatusMaster;
import com.claim.claim_processing.common.entities.loan_master.LoanTypeMaster;

@Entity
@Table(
        name = "CLAIM_APPLICATION_LOAN_DETAIL",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationLoanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * One loan detail belongs to one deduction detail
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "DEDUCTION_DETAIL_ID",
            nullable = true,
            unique = true,
            foreignKey = @ForeignKey(name = "FK_CALD_DEDUCTION_DETAIL")
    )
    private ClaimApplicationDeductionDetail deductionDetail;

    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_CADD_DED_CLAIM_APP"))
        private ClaimApplication claimApplication;

    @Column(name = "LOAN_ACCOUNT_NUMBER", length = 100)
    private String loanAccountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "LOAN_TYPE_ID",
            foreignKey = @ForeignKey(name = "FK_CALD_LOAN_TYPE")
    )
    private LoanTypeMaster loanType;

    @Column(name = "SANCTION_DATE")
    private LocalDate sanctionDate;

    @Column(name = "EMI_START_DATE")
    private LocalDate emiStartDate;

    @Column(name = "EMI_END_DATE")
    private LocalDate emiEndDate;

    @Column(name = "IS_GUARANTEE", length = 1)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivityEnum isGuarantee = ActivityEnum.N;

    @Column(name = "OUTSTANDING_PRINCIPAL", precision = 15, scale = 2)
    private BigDecimal outstandingPrincipal;

    @Column(name = "OUTSTANDING_INTEREST", precision = 15, scale = 2)
    private BigDecimal outstandingInterest;

    @Column(name = "PENALTY_AMOUNT", precision = 15, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(name = "TOTAL_OUTSTANDING", precision = 15, scale = 2)
    private BigDecimal totalOutstanding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "LOAN_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CALD_LOAN_STATUS")
    )
    private LoanStatusMaster loanStatus;

    @Column(name = "LOAN_START_DATE")
    private LocalDate loanStartDate;

    @Column(name = "LOAN_END_DATE")
    private LocalDate loanEndDate;

    @Column(name = "LOAN_SNAPSHOT_DATE")
    private LocalDate loanSnapshotDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "LOAN_REVIEW_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CALD_LOAN_REVIEW_STATUS")
    )
    private ReviewStatusMaster loanReviewStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "LOAN_ADJUSTMENT_DECISION_ID",
            foreignKey = @ForeignKey(name = "FK_CALD_LOAN_ADJ_DECISION")
    )
    private DecisionMaster loanAdjustmentDecision;

    @Column(name = "VERIFIED_LOAN_DEDUCTION_AMOUNT", precision = 15, scale = 2)
    private BigDecimal verifiedLoanDeductionAmount;

    @Column(name = "VERIFIED_OUTSTANDING_BALANCE", precision = 15, scale = 2)
    private BigDecimal verifiedOutstandingBalance;

    @Column(name = "LOAN_RECOVERY_HISTORY_TEXT", length = 2000)
    private String loanRecoveryHistoryText;

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