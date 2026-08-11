package com.claim.claim_processing.application.entity.application;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.claim.claim_processing.application.entity.calculation.*;
import com.claim.claim_processing.application.entity.detail.*;
import com.claim.claim_processing.application.entity.workFlow.*;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.common.*;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.SchemeType;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;

@Entity
@Table(
        name = "CLAIM_APPLICATION",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA",
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

    @Column(name = "IDENTITY_NUMBER", length = 100)
    private String identityNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_TYPE_ID", nullable = true)
    private ClaimTypeMaster claimType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBMISSION_CHANNEL_ID")
    private SubmissionChannelMaster submissionChannel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEME_TYPE_ID", referencedColumnName = "SCHEME_TYPE_ID")
    private SchemeType schemeType;

    @Column(name = "PF_START_DATE")
    private LocalDate pfStartDate;

    @Column(name = "PF_END_DATE")
    private LocalDate pfEndDate;

    @Column(name = "PENSION_START_DATE")
    private LocalDate pensionStartDate;

    @Column(name = "PENSION_END_DATE")
    private LocalDate pensionEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_CATEGORY_ID", referencedColumnName = "CATEGORY_ID")
    private AgencyCategory memberCategory;

    @Column(name = "EMPLOYMENT_TYPE", length = 100)
    private String employmentType;

    @Column(name = "MEMBER_CODE", length = 100)
    private String memberCode;

    @Column(name = "CONTACT_NO", length = 100)
    private String contactNo;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "NPPF_NUMBER", length = 100)
    private String nppfNumber;

    @Column(name = "AGENCY_CODE", length = 100)
    private String agencyCode;

    @Column(name = "OFFICE_ID")
    private Long officeId;

    @Column(name = "APPLICATION_DATE", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "ON_BEHALF_OF_MEMBER", length = 1)
    @Builder.Default
    private String onBehalfOfMember = "N";

    @Column(name = "INITIATED_BY", length = 100)
    private String initiatedBy;

    @Column(name = "CLAIMED_BY", length = 100)
    private String claimedBy;

    @Column(name = "UNCLAIMED_BY", length = 100)
    private String unClaimedBy;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_SPECIAL_CASE", length = 1)
    private ActivityEnum isSpecialCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_RENTAL_APPLIED", length = 1)
    private ActivityEnum isRentalApplied;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_LOAN_APPLIED", length = 1)
    private ActivityEnum isLoanApplied;

    @Column(name = "CURRENCY_CODE", length = 10)
    private String currencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_STAGE_ID")
    private StageMaster currentStage;

    /**
     * STATUS_ID references STATUS_MASTER(STATUS_ID), not ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID")
    private StatusMaster status;

    @Column(name = "NUMBER_OF_YEAR_IN_SERVICE", length = 100)
    private BigDecimal numberOfYearInService;

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

    @Column(name = "PENSION_APPLICATION_REF", length = 100)
    private String pensionApplicationRef;

    @Column(name = "PENSION_TRIGGER_STATUS", length = 20)
    private String pensionTriggerStatus;


    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private NormalClaimDetail normalClaimDetail;

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PartialWithdrawalDetail partialWithdrawalDetail;

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WrongRemitance> wrongRemitances;

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BeneficiarySettlementDetail beneficiarySettlementDetail;

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LegalRecoveryDetail legalRecoveryDetail;

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ClaimSpecialCaseApplication claimSpecialCaseApplication;

    // // ---------- One-to-Many Common Child Tables ----------

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationBankDetail> bankDetails = new ArrayList<>();

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClaimApplicationDeductionDetail deductionDetail;

    // // ---------- Calculation ----------

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClaimApplicationCalculationSummary calculationSummary;
    
    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClaimApplicationApproval approvalDetail;

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationForfeitedComponent> forfeitedComponents = new ArrayList<>();

    @OneToMany(mappedBy = "claimApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationWorkflow> workflows = new ArrayList<>();

    @OneToOne(mappedBy = "claimApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ClaimApplicationVerification verificationDetail;

    
    @PrePersist
    public void prePersist() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());

        if (this.isSpecialCase == null) {
            this.isSpecialCase = ActivityEnum.N;
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
