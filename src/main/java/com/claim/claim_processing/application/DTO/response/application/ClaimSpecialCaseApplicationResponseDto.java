package com.claim.claim_processing.application.DTO.response.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto.SpecialCaseComponentBalanceDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSpecialCaseApplicationResponseDto {

    private Long id;

    // Reference to the claim
    private Long claimApplicationId;
    private String applicationNumber;

    // Special Case Information
    private String caseType;
    private Long caseReasonId;
    private String caseReasonName;

    // Amount Details
    private String pensionType;
    private LocalDate pensionStartDate;
    private Integer totalContributionYears;
    private BigDecimal totalPensionAmount;

    // For Pension Conversion
    private String currentBenefitType;
    private String requestedBenefitType;

    // For Forfeited Repayment
    private BigDecimal approvedAmount;
    private BigDecimal totalForfeitedAmount;
    private BigDecimal eligibleClaimAmount;
    private LocalDateTime forfeitedDate;
    private String componentCodes;

    private String approvedBy;
    private LocalDateTime approvedDate;
    private String approvalReference;
    private String rejectionReason;

    private Long pensionAccountId;
    // Reserve Account Reference
    private Long reserveAccountId;

    // Components
    private List<SpecialCaseComponentBalanceResponseDTO> components;

    // Audit Information
    private String isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialCaseComponentBalanceResponseDTO {
        private String code; // PF_MC, PF_IMC, PF_EC, etc.
        private String name; // Component display name
        private String type; // CONTRIBUTION, INTEREST, FORFEITED, DEDUCTION
        private BigDecimal amount; // Component amount
        private BigDecimal percentalAmount; // Percentage amount (for partial calculations)
    }
}