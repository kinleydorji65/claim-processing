package com.claim.claim_processing.application.entity.application;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.entities.pension.PensionDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    // Special Case Information
    @Column(name = "CASE_TYPE", nullable = false, length = 50)
    private String caseType;

    @Column(name = "CASE_REASON_ID")
    private Long caseReasonId;

    // Pension Details (snapshot at time of application)
    @Column(name = "PENSION_TYPE", length = 50)
    private String pensionType;

    @Column(name = "PENSION_START_DATE")
    private LocalDate pensionStartDate;

    @Column(name = "TOTAL_CONTRIBUTION_YEARS")
    private Integer totalContributionYears;

    @Column(name = "TOTAL_PENSION_AMOUNT", precision = 18, scale = 2)
    private BigDecimal totalPensionAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PENSION_ACCOUNT_ID")
    private PensionDetail pensionAccount;

    // For Pension Conversion
    @Column(name = "CURRENT_BENEFIT_TYPE", length = 20)
    private String currentBenefitType;

    @Column(name = "REQUESTED_BENEFIT_TYPE", length = 20)
    private String requestedBenefitType;

    // For Forfeited Repayment (snapshot at time of application)
    @Column(name = "TOTAL_FORFEITED_AMOUNT", precision = 18, scale = 2)
    private BigDecimal totalForfeitedAmount;

    @Column(name = "ELIGIBLE_CLAIM_AMOUNT", precision = 18, scale = 2)
    private BigDecimal eligibleClaimAmount;

    @Column(name = "FORFEITED_DATE")
    private LocalDateTime forfeitedDate;

    @Column(name = "COMPONENT_CODES", length = 500)
    private String componentCodes;

    // Amount Details
    @Column(name = "REQUESTED_AMOUNT", precision = 18, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "APPROVED_AMOUNT", precision = 18, scale = 2)
    private BigDecimal approvedAmount;

    // Approval Information
    @Column(name = "APPROVED_BY", length = 100)
    private String approvedBy;

    @Column(name = "APPROVED_DATE")
    private LocalDateTime approvedDate;

    @Column(name = "APPROVAL_REFERENCE", length = 50)
    private String approvalReference;

    @Column(name = "REJECTION_REASON", length = 500)
    private String rejectionReason;

    // Reserve Account Reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESERVE_ACCOUNT_ID")
    private ReserveAccount reserveAccount;

    // Audit Information
    @Column(name = "IS_ACTIVE", length = 1)
    private String isActive;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (isActive == null) {
            isActive = "Y";
        }
        if (requestedAmount == null) {
            requestedAmount = BigDecimal.ZERO;
        }
        if (approvedAmount == null) {
            approvedAmount = BigDecimal.ZERO;
        }
        if (totalForfeitedAmount == null) {
            totalForfeitedAmount = BigDecimal.ZERO;
        }
        if (eligibleClaimAmount == null) {
            eligibleClaimAmount = BigDecimal.ZERO;
        }
        if (totalPensionAmount == null) {
            totalPensionAmount = BigDecimal.ZERO;
        }
    }

    // Helper Methods
    public void approve(String approvedBy, String reference) {
        this.approvedBy = approvedBy;
        this.approvedDate = LocalDateTime.now();
        this.approvalReference = reference;
        this.updatedBy = approvedBy;
    }

    public void reject(String rejectedBy, String reason) {
        this.rejectionReason = reason;
        this.updatedBy = rejectedBy;
    }

    public void process(String processedBy) {
        this.updatedBy = processedBy;
    }

    /**
     * Calculate eligible claim amount (80% of total forfeited)
     */
    public BigDecimal calculateEligibleClaimAmount() {
        if (totalForfeitedAmount == null || totalForfeitedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return totalForfeitedAmount.multiply(BigDecimal.valueOf(0.8))
            .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Check if application is active
     */
    public boolean isActive() {
        return "Y".equalsIgnoreCase(isActive);
    }

    /**
     * Deactivate the application
     */
    public void deactivate(String deactivatedBy) {
        this.isActive = "N";
        this.updatedBy = deactivatedBy;
    }

    /**
     * Get approval status (derived field)
     */
    public String getApprovalStatus() {
        if (approvedBy != null && approvedDate != null) {
            return "APPROVED";
        } else if (rejectionReason != null) {
            return "REJECTED";
        } else {
            return "PENDING";
        }
    }
}