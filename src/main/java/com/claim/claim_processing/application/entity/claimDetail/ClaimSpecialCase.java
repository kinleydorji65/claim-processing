package com.claim.claim_processing.application.entity.claimDetail;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.claim.claim_processing.common.entities.claim.ReserveAccount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CLAIM_SPECIAL_CASE", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ClaimSpecialCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_DETAIL_ID", nullable = false)
    private ClaimDetail claimDetail;

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

    @Column(name = "PENSION_ACCOUNT_ID")
    private Long pensionAccountId;

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

    // ✅ Components Relationship
    @OneToMany(mappedBy = "specialCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<ClaimSpecialCaseComponentDetail> componentDetails = new ArrayList<>();

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
        if (componentDetails == null) {
            componentDetails = new ArrayList<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // =============================================
    // COMPONENT MANAGEMENT HELPER METHODS
    // =============================================

    /**
     * Add a component detail to the special case
     */
    public void addComponentDetail(ClaimSpecialCaseComponentDetail componentDetail) {
        if (componentDetail == null) {
            return;
        }
        if (componentDetails == null) {
            componentDetails = new ArrayList<>();
        }
        componentDetails.add(componentDetail);
        componentDetail.setSpecialCase(this);
    }

    /**
     * Add multiple component details to the special case
     */
    public void addComponentDetails(List<ClaimSpecialCaseComponentDetail> componentDetails) {
        if (componentDetails == null || componentDetails.isEmpty()) {
            return;
        }
        for (ClaimSpecialCaseComponentDetail detail : componentDetails) {
            addComponentDetail(detail);
        }
    }

    /**
     * Remove a component detail from the special case
     */
    public void removeComponentDetail(ClaimSpecialCaseComponentDetail componentDetail) {
        if (componentDetail == null || componentDetails == null) {
            return;
        }
        componentDetails.remove(componentDetail);
        componentDetail.setSpecialCase(null);
    }

    /**
     * Clear all component details
     */
    public void clearComponentDetails() {
        if (componentDetails != null) {
            componentDetails.clear();
        }
    }

    /**
     * Get active component details only
     */
    public List<ClaimSpecialCaseComponentDetail> getActiveComponentDetails() {
        if (componentDetails == null) {
            return new ArrayList<>();
        }
        return componentDetails.stream()
                .filter(detail -> "Y".equalsIgnoreCase(detail.getIsActive()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get components by type
     */
    public List<ClaimSpecialCaseComponentDetail> getComponentDetailsByType(String componentType) {
        if (componentDetails == null || componentType == null) {
            return new ArrayList<>();
        }
        return componentDetails.stream()
                .filter(detail -> componentType.equalsIgnoreCase(detail.getComponentType()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get eligible components
     */
    public List<ClaimSpecialCaseComponentDetail> getEligibleComponents() {
        return getComponentDetailsByType("ELIGIBLE");
    }

    /**
     * Get forfeited components
     */
    public List<ClaimSpecialCaseComponentDetail> getForfeitedComponents() {
        return getComponentDetailsByType("FORFEITED");
    }

    /**
     * Get deduction components
     */
    public List<ClaimSpecialCaseComponentDetail> getDeductionComponents() {
        return getComponentDetailsByType("DEDUCTION");
    }

    /**
     * Get interest components
     */
    public List<ClaimSpecialCaseComponentDetail> getInterestComponents() {
        return getComponentDetailsByType("INTEREST");
    }

    /**
     * Calculate total amount of all components
     */
    public BigDecimal calculateTotalComponentAmount() {
        if (componentDetails == null || componentDetails.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return componentDetails.stream()
                .filter(detail -> "Y".equalsIgnoreCase(detail.getIsActive()))
                .map(ClaimSpecialCaseComponentDetail::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate total amount by component type
     */
    public BigDecimal calculateTotalComponentAmountByType(String componentType) {
        if (componentDetails == null || componentDetails.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return componentDetails.stream()
                .filter(detail -> "Y".equalsIgnoreCase(detail.getIsActive()))
                .filter(detail -> componentType.equalsIgnoreCase(detail.getComponentType()))
                .map(ClaimSpecialCaseComponentDetail::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // =============================================
    // APPROVAL HELPER METHODS
    // =============================================

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
        } else if (rejectionReason != null && !rejectionReason.isEmpty()) {
            return "REJECTED";
        } else {
            return "PENDING";
        }
    }
}