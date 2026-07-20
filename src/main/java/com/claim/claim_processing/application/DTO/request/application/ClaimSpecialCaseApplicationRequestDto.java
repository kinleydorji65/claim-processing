package com.claim.claim_processing.application.DTO.request.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.claim.claim_processing.common.entities.common.activityEnum.CaseTypeEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSpecialCaseApplicationRequestDto {
    private Long id;

    private CaseTypeEnum caseType;

    private Long caseReasonId;

    private String pensionType;

    private LocalDate pensionStartDate;

    private Integer totalContributionYears;

    private BigDecimal totalPensionAmount;

    private Long pensionAccountId;

    private String currentBenefitType;

    private String requestedBenefitType;

    private BigDecimal totalForfeitedAmount;

    private BigDecimal eligibleClaimAmount;

    private LocalDateTime forfeitedDate;

    private String componentCodes;

    private BigDecimal requestedAmount;

    private BigDecimal approvedAmount;

    private String approvedBy;

    private LocalDateTime approvedDate;

    private String approvalReference;

    private String rejectionReason;

    private Long reserveAccountId;

    private String createdBy;

    private String updatedBy;

    private List<SpecialCaseComponentBalanceDTO> componentBalances;

    @Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class SpecialCaseComponentBalanceDTO {
    private String code;          // PF_MC, PF_IMC, PF_EC, etc.
    private String name;          // Component display name
    private String type;          // CONTRIBUTION, INTEREST, FORFEITED, DEDUCTION
    private BigDecimal amount;    // Component amount
    private BigDecimal percentalAmount; // Percentage amount (for partial calculations)
}
}