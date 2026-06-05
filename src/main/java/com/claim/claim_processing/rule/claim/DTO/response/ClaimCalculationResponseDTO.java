package com.claim.claim_processing.rule.claim.DTO.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.claim.claim_processing.integration.loanAdjustment.dto.LoanAdjustmentResultDto;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;

@Data
@Builder
public class ClaimCalculationResponseDTO {
    private String nppfNumber;
    private BigDecimal noOfYearInService;
    private String subClaimCode;
    private boolean loanCheck;
    private boolean rentalCheck;
    
    // Service period
    private LocalDate contributionStartDate;
    private LocalDate contributionEndDate;

    private Integer totalContributionMonths;
    private Integer totalNonContributionMonths;
    private String eligibilityNote;
    private LoanAdjustmentResultDto laoanAdjustmentResult;

    private String vestingNote;

    private String recommendedBenefitType;

    private List<String> forfeitedComponents;
    private BigDecimal totalPfAmount;
    private BigDecimal totalPensionAmount;

    private BigDecimal totalPensionInterestAmount;
    private BigDecimal totalPfInterestAmount;

    private EligibilityEnum pfIsEligible;
    private EligibilityEnum pensionIsEligible;
    private BigDecimal finalPayableAmount;
    private String adjustmentNote;
    
    // Component balances (raw components from Table 1)
    private List<ComponentBalanceDTO> components;
    private List<ExpressionCalculationDTO> expressionCalculations;
    
    
    @Data
    @Builder
    public static class ExpressionCalculationDTO {
        private String expression;          // MC+IMC
        private List<String> resolvedCodes; // PF_MC, PF_IMC
        private BigDecimal expressionAmount; // 5200
        private BigDecimal withdrawalPercentage;
        private String type;                // ELIGIBLE / FORFEITED
    }
    @Data
    @Builder
    public static class ComponentBalanceDTO {
        private String code;        // PF_MC, PF_IMC, PF_EC, etc.
        private String name;
        private String type;        // CONTRIBUTION or INTEREST
        private BigDecimal amount;
    }

}

