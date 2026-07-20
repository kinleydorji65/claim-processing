package com.claim.claim_processing.rule.claim.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;

@Data
@Builder
public class ClaimCalculationResponseDTO {
    private String nppfNumber;
    private BigDecimal noOfYearInService;
    private boolean loanCheck;
    private boolean rentalCheck;

    // Service period
    private LocalDate contributionStartDate;
    private LocalDate contributionEndDate;

    private Integer totalContributionMonths;
    private Integer totalNonContributionMonths;
    private String eligibilityNote;
    private String loanNote;
    private String rentalNote;

    private String vestingNote;

    private String recommendedBenefitType;

    private List<ComponentBalanceDTO> forfeitedComponents;
    private BigDecimal totalPfAmount;
    private BigDecimal totalPensionAmount;
    private BigDecimal totalAmount;

    private BigDecimal totalPensionInterestAmount;
    private BigDecimal totalPfInterestAmount;

    private EligibilityEnum pfIsEligible;
    private EligibilityEnum pensionIsEligible;
    private BigDecimal finalPayableAmount;

    // Component balances (raw components from Table 1)
    private List<ComponentBalanceDTO> components;
    private List<ExpressionCalculationDTO> expressionCalculations;

    @Data
    @Builder
    public static class ExpressionCalculationDTO {
        private String expression; // MC+IMC
        private List<String> resolvedCodes; // PF_MC, PF_IMC
        private BigDecimal expressionAmount; // 5200
        private BigDecimal precentalWithDrawalAmount;
        private BigDecimal withdrawalPercentage;
        private String type; // ELIGIBLE / FORFEITED
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComponentBalanceDTO {
        private String subRuleCode; // PF_MC, PF_IMC, etc.
        private String code; // PF_MC, PF_IMC, PF_EC, etc.
        private String name;
        private String type; // CONTRIBUTION or INTEREST
        private BigDecimal amount;
        private BigDecimal percentalAmount;
    }

    @Data
    @Builder
    public static class DeductionAdjustmentResultDto {

        private String deductionCategory;

        private String referenceName;

        private BigDecimal outstandingAmount;

        private BigDecimal deductedAmount;

        private BigDecimal remainingAmount;
    }

}
