package com.claim.claim_processing.application.DTO.response.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSpecialCaseApplicationResponseDto {

    private Long id;

    // Reference to the claim
    private Long claimApplicationId;

    // Member Information
    private String memberCode;
    private String nppfNumber;
    private String identityNumber;

    // Agency Information
    private String agencyCategoryId;
    private String agencyCode;

    // Special Case Information
    private String caseType;
    private String caseReason;

    // Amount Details
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;

    // For Pension Conversion
    private String currentBenefitType;
    private String requestedBenefitType;

    // For Forfeited Repayment
    private String forfeitedComponentCodes;

    // Approval Information
    private LocalDateTime requestDate;
    private String requestedBy;
    private String approvedBy;
    private LocalDateTime approvedDate;
    private String approvalReference;
    private String rejectionReason;

    // Processing Information
    private String processedBy;
    private LocalDateTime processedDate;

    // Reserve Account Reference
    private Long reserveAccountId;

    // Audit Information
    private String isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    
}
