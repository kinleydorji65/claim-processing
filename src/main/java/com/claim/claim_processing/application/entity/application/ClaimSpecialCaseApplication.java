package com.claim.claim_processing.application.entity.application;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_SPECIAL_CASE_APPLICATION", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSpecialCaseApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // Reference to the claim
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, unique = true)
    private ClaimApplication claimApplication;

    // Member Information
    @Column(name = "MEMBER_CODE")
    private String memberCode;

    @Column(name = "NPPF_NUMBER")
    private String nppfNumber;

    @Column(name = "IDENTITY_NUMBER")
    private String identityNumber;

    // Agency Information
    @Column(name = "AGENCY_CATEGORY_ID")
    private String agencyCategoryId;

    @Column(name = "AGENCY_CODE")
    private String agencyCode;

    // Special Case Information
    @Column(name = "CASE_TYPE", nullable = false)
    private String caseType;

    @Column(name = "CASE_REASON")
    private String caseReason;

    // Amount Details
    @Column(name = "REQUESTED_AMOUNT")
    private BigDecimal requestedAmount;

    @Column(name = "APPROVED_AMOUNT")
    private BigDecimal approvedAmount;

    // For Pension Conversion
    @Column(name = "CURRENT_BENEFIT_TYPE")
    private String currentBenefitType;

    @Column(name = "REQUESTED_BENEFIT_TYPE")
    private String requestedBenefitType;

    // For Forfeited Repayment
    @Column(name = "FORFEITED_COMPONENT_CODES")
    private String forfeitedComponentCodes;

    // Approval Information
    @Column(name = "REQUEST_DATE")
    private LocalDateTime requestDate;

    @Column(name = "REQUESTED_BY")
    private String requestedBy;

    @Column(name = "APPROVED_BY")
    private String approvedBy;

    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;

    @Column(name = "APPROVAL_REFERENCE")
    private String approvalReference;

    @Column(name = "REJECTION_REASON")
    private String rejectionReason;

    // Processing Information
    @Column(name = "PROCESSED_BY")
    private String processedBy;

    @Column(name = "PROCESSED_DATE")
    private LocalDateTime processedDate;

    // Reserve Account Reference
    @Column(name = "RESERVE_ACCOUNT_ID")
    private Long reserveAccountId;

    // Audit Information
    @Column(name = "IS_ACTIVE")
    private String isActive;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (requestDate == null) {
            requestDate = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = "Y";
        }
        if (requestedAmount == null) {
            requestedAmount = BigDecimal.ZERO;
        }
        if (approvedAmount == null) {
            approvedAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper Methods
    public void approve(String approvedBy, String reference) {
        this.approvedBy = approvedBy;
        this.approvedDate = LocalDateTime.now();
        this.approvalReference = reference;
        this.updatedBy = approvedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String rejectedBy, String reason) {
        this.rejectionReason = reason;
        this.updatedBy = rejectedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void process(String processedBy) {
        this.processedBy = processedBy;
        this.processedDate = LocalDateTime.now();
        this.updatedBy = processedBy;
        this.updatedAt = LocalDateTime.now();
    }
}
