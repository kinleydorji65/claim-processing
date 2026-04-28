package com.claim.claim_processing.claim.DTO.response.application;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationTdsTaxResponseDto {

    private Long id;

    // ---------------------------------
    // Deduction Detail
    // ---------------------------------
    private Long deductionDetailId;

    // ---------------------------------
    // Tax Calculation
    // ---------------------------------
    private BigDecimal taxableAmount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    // ---------------------------------
    // Tax Rule / Section
    // ---------------------------------
    private String taxRuleCode;
    private String taxSectionCode;

    // ---------------------------------
    // Taxpayer Info
    // ---------------------------------
    private String panOrTaxPayerId;
    private String fiscalYear;

    // ---------------------------------
    // Tax Dates / References
    // ---------------------------------
    private LocalDate taxDeductionDate;
    private String tdsCertificateNumber;
    private String challanReferenceNumber;

    // ---------------------------------
    // Deposit Status
    // ---------------------------------
    private Long taxDepositStatusId;
    private String taxDepositStatusCode;
    private String taxDepositStatusName;

    // ---------------------------------
    // Remarks
    // ---------------------------------
    private String remarks;

    // ---------------------------------
    // Audit
    // ---------------------------------
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
