package com.claim.claim_processing.rule.pension.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensionDetailResponseDTO {
    
    // Pension Detail ID
    private Long pensionDetailId;
    
    // Member Identification
    private String nppfNumber;
    private String memberIdentityNumber;
    private String agencyCode;
    private String currencyCode;
    
    // Pension Classification
    private String pensionType;
    private String pensionCategory;
    private String pensionSubCategory;
    
    // Pension Amounts
    private BigDecimal monthlyPensionAmount;
    private BigDecimal totalPensionFund;
    
    // Reference Information
    private Integer totalContributionMonths;
    private Integer totalContributionYears;
    
    // Pension Dates
    private LocalDate pensionStartDate;
    private LocalDate pensionEndDate;
    private LocalDate retirementDate;
    
    // Pension Status
    private String pensionStatus;
    
    // Bank Details
    private Long bankTypeId;
    private String bankName;
    private String bankAccountNumber;
    private String accountHolderName;
    private String ifscCode;
    
    // Audit Fields
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
