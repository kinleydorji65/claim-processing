package com.claim.claim_processing.common.entities.pension;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PENSION_DETAIL", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NPPF_NUMBER", length = 50, nullable = false)
    private String nppfNumber;

    @Column(name = "MEMBER_IDENTITY_NUMBER", length = 50)
    private String memberIdentityNumber;

    @Column(name = "AGENCY_CODE", length = 50)
    private String agencyCode;

    @Column(name = "CURRENCY_CODE", length = 10)
    private String currencyCode;

    // Pension Classification
    @Column(name = "PENSION_TYPE", length = 50)
    private String pensionType;

    @Column(name = "PENSION_CATEGORY", length = 50)
    private String pensionCategory;

    // Pension Amounts
    @Column(name = "MONTHLY_PENSION_AMOUNT", precision = 18, scale = 2)
    private BigDecimal monthlyPensionAmount;

    @Column(name = "TOTAL_PENSION_FUND", precision = 18, scale = 2)
    private BigDecimal totalPensionFund;

    // Reference Information
    @Column(name = "TOTAL_CONTRIBUTION_MONTHS")
    private Integer totalContributionMonths;

    @Column(name = "TOTAL_CONTRIBUTION_YEARS")
    private Integer totalContributionYears;

    // Pension Dates
    @Column(name = "PENSION_START_DATE")
    private LocalDate pensionStartDate;

    @Column(name = "PENSION_END_DATE")
    private LocalDate pensionEndDate;

    @Column(name = "RETIREMENT_DATE")
    private LocalDate retirementDate;

    // Pension Status
    @Column(name = "PENSION_STATUS", length = 30)
    private String pensionStatus;

    // Bank Details
    @Column(name = "BANK_TYPE_ID")
    private Long bankTypeId;

    @Column(name = "BANK_NAME", length = 100)
    private String bankName;

    @Column(name = "BANK_ACCOUNT_NUMBER", length = 50)
    private String bankAccountNumber;

    @Column(name = "ACCOUNT_HOLDER_NAME", length = 100)
    private String accountHolderName;

    @Column(name = "IFSC_CODE", length = 50)
    private String ifscCode;

    // Audit Fields
    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    // Helper methods
    @PrePersist
    protected void onCreate() {
        if (currencyCode == null) {
            currencyCode = "BTN";
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
