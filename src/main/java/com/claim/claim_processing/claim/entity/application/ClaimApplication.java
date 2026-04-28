package com.claim.claim_processing.claim.entity.application;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.claim.claim_processing.claim.entity.calculation.*;
import com.claim.claim_processing.claim.entity.detail.*;
import com.claim.claim_processing.claim.entity.payment.ClaimApplicationPayment;
import com.claim.claim_processing.claim.entity.workFlow.*;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.common.*;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;

@Entity
@Table(
        name = "CLAIM_APPLICATION",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_CLAIM_APPLICATION_NUMBER", columnNames = "APPLICATION_NUMBER")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "APPLICATION_NUMBER", nullable = false, length = 100)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_TYPE_ID", nullable = false)
    private ClaimTypeMaster claimType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_SOURCE_ID")
    private ClaimSourceMaster claimSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBMISSION_CHANNEL_ID")
    private SubmissionChannelMaster submissionChannel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEME_TYPE_ID")
    private SchemeMaster schemeType;

    @Column(name = "TOTAL_CONTRIBUTION_MONTHS")
    private Integer totalContributionMonths;

    @Column(name = "TOTAL_CONTRIBUTION_YEARS")
    private Integer totalContributionYears;

    @Column(name = "CONTRIBUTION_START_DATE")
    private LocalDate contributionStartDate;

    @Column(name = "CONTRIBUTION_END_DATE")
    private LocalDate contributionEndDate;

    /**
     * MEMBER_CATEGORY_ID is VARCHAR2(50) and references AGENCY_CATEGORIES(CATEGORY_ID)
     * So map this only if CATEGORY_ID in AGENCY_CATEGORIES is also String/VARCHAR.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_CATEGORY_ID", referencedColumnName = "CATEGORY_ID")
    private AgencyCategory memberCategory;

    @Column(name = "EMPLOYMENT_TYPE", length = 100)
    private String employmentType;

    @Column(name = "MEMBER_CODE", length = 100)
    private String memberCode;

    @Column(name = "NPPF_NUMBER", length = 100)
    private String nppfNumber;

    @Column(name = "AGENCY_CODE", length = 100)
    private String agencyCode;

    @Column(name = "OFFICE_ID")
    private Long officeId;

    @Column(name = "APPLICATION_DATE", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "SUBMITTED_BY", length = 100)
    private String submittedBy;

    @Column(name = "INITIATED_BY", length = 100)
    private String initiatedBy;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_SPECIAL_CASE", length = 1)
    private ActivityEnum isSpecialCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "REQUIRES_MANUAL_REVIEW", length = 1)
    private ActivityEnum requiresManualReview;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_FINANCIAL_CASE", length = 1)
    private ActivityEnum isFinancialCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_PAYMENT_REQUIRED", length = 1)
    private ActivityEnum isPaymentRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_POSTING_REQUIRED", length = 1)
    private ActivityEnum isPostingRequired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CLAIM_APPLICATION_ID")
    private ClaimApplication parentClaimApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIAL_CASE_AUTHORITY_ID")
    private SpecialCaseRefundAuthorityMaster specialCaseAuthority;

    @Column(name = "CURRENCY_CODE", length = 10)
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "REQUIRES_MANUAL_APPROVAL", length = 1)
    private ActivityEnum requiresManualApproval;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_STAGE_ID")
    private StageMaster currentStage;

    /**
     * STATUS_ID references STATUS_MASTER(STATUS_ID), not ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID")
    private StatusMaster status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTION_ID")
    private ActionMaster action;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_ACTIVE", length = 1)
    private ActivityEnum isActive;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;


    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private NormalClaimDetail normalClaimDetail;

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PartialWithdrawalDetail partialWithdrawalDetail;

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BeneficiarySettlementDetail beneficiarySettlementDetail;

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ExcessRefundDetail excessRefundDetail;

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LegalRecoveryDetail legalRecoveryDetail;

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private WrongRemittanceDetail wrongRemittanceDetail;

    // // ---------- One-to-Many Common Child Tables ----------

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationBankDetail> bankDetails = new ArrayList<>();

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationDeductionDetail> deductionDetails = new ArrayList<>();

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationLoanDetail> loanDetails = new ArrayList<>();

    // // ---------- Calculation ----------

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationCalculationSummary> calculationSummaries = new ArrayList<>();

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationRuleEvaluation> ruleEvaluations = new ArrayList<>();

    // // ---------- Payment ----------

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationPayment> payments = new ArrayList<>();

    // ---------- Work Flow ----------

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationVerification> verifications = new ArrayList<>();

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationApproval> approvals = new ArrayList<>();

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationWorkflow> workflows = new ArrayList<>();


    // ---------- Posting ----------

    // @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    // @Builder.Default
    // private List<ClaimApplicationPosting> postings = new ArrayList<>();

    
    @PrePersist
    public void prePersist() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());

        if (this.isSpecialCase == null) {
            this.isSpecialCase = ActivityEnum.N;
        }
        if (this.requiresManualReview == null) {
            this.requiresManualReview = ActivityEnum.N;
        }
        if (this.isFinancialCase == null) {
            this.isFinancialCase = ActivityEnum.N;
        }
        if (this.isPaymentRequired == null) {
            this.isPaymentRequired = ActivityEnum.N;
        }
        if (this.isPostingRequired == null) {
            this.isPostingRequired = ActivityEnum.N;
        }
        if (this.requiresManualApproval == null) {
            this.requiresManualApproval = ActivityEnum.N;
        }
        if (this.isActive == null) {
            this.isActive = ActivityEnum.Y;
        }
        if (this.currencyCode == null) {
            this.currencyCode = "BTN";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
