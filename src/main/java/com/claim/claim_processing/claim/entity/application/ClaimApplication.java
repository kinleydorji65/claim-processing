package com.claim.claim_processing.claim.entity.application;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.common.ActionMaster;
import com.claim.claim_processing.common.entities.common.ClaimSourceMaster;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;
import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.entities.special_case.SpecialCaseRefundAuthorityMaster;

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

    @Column(name = "CLAIM_REFERENCE_NUMBER", length = 100)
    private String claimReferenceNumber;

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

    @Column(name = "IS_SPECIAL_CASE", length = 1)
    private Character isSpecialCase;

    @Column(name = "REQUIRES_MANUAL_REVIEW", length = 1)
    private Character requiresManualReview;

    @Column(name = "IS_FINANCIAL_CASE", length = 1)
    private Character isFinancialCase;

    @Column(name = "IS_PAYMENT_REQUIRED", length = 1)
    private Character isPaymentRequired;

    @Column(name = "IS_POSTING_REQUIRED", length = 1)
    private Character isPostingRequired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CLAIM_APPLICATION_ID")
    private ClaimApplication parentClaimApplication;

    @Column(name = "RELATED_CASE_REFERENCE", length = 100)
    private String relatedCaseReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIAL_CASE_AUTHORITY_ID")
    private SpecialCaseRefundAuthorityMaster specialCaseAuthority;

    @Column(name = "CURRENCY_CODE", length = 10)
    private String currencyCode;

    @Column(name = "REQUIRES_MANUAL_APPROVAL", length = 1)
    private Character requiresManualApproval;

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

    @Column(name = "IS_ACTIVE", length = 1)
    private Character isActive;

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
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());

        if (this.isSpecialCase == null) {
            this.isSpecialCase = 'N';
        }
        if (this.requiresManualReview == null) {
            this.requiresManualReview = 'N';
        }
        if (this.isFinancialCase == null) {
            this.isFinancialCase = 'N';
        }
        if (this.isPaymentRequired == null) {
            this.isPaymentRequired = 'N';
        }
        if (this.isPostingRequired == null) {
            this.isPostingRequired = 'N';
        }
        if (this.requiresManualApproval == null) {
            this.requiresManualApproval = 'N';
        }
        if (this.isActive == null) {
            this.isActive = 'Y';
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
