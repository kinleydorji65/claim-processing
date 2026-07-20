package com.claim.claim_processing.application.DTO.response.claimDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSpecialCaseResponse {
    private Long id;
    private Long claimDetailId;

    // Special Case Information
    private String caseType;
    private Long caseReasonId;

    // Pension Details (snapshot at time of application)
    private String pensionType;

    private LocalDate pensionStartDate;

    private Integer totalContributionYears;

    private BigDecimal totalPensionAmount;

    private Long pensionAccountId;

    // For Pension Conversion
    private String currentBenefitType;

    private String requestedBenefitType;

    // For Forfeited Repayment (snapshot at time of application)
    private BigDecimal totalForfeitedAmount;

    private BigDecimal eligibleClaimAmount;

    private LocalDateTime forfeitedDate;

    private String componentCodes;

    // Amount Details
    private BigDecimal requestedAmount;

    private BigDecimal approvedAmount;

    // Approval Information
    private String approvedBy;

    private LocalDateTime approvedDate;

    private String approvalReference;

    private String rejectionReason;

    // Reserve Account Reference
    private Long reserveAccountId;
    private Long pensionDetailId; // Added to include pension detail reference

    // Audit Information
    private String isActive;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    private List<SpecialCaseComponentBalanceResponseDTO> components;

     @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialCaseComponentBalanceResponseDTO {
        private Long id;
        private String code; // PF_MC, PF_IMC, PF_EC, etc.
        private String name; // Component display name
        private String type; // CONTRIBUTION, INTEREST, FORFEITED, DEDUCTION
        private BigDecimal amount; // Component amount
        private BigDecimal percentalAmount; // Percentage amount (for partial calculations)
    }
}
