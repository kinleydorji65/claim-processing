package com.claim.claim_processing.application.DTO.response.claimDetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimCalculationSummaryResponseDto {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate calculationEffectiveDate;

    private BigDecimal finalPayableAmount;
    private BigDecimal actualAmountCalculated;
    private BigDecimal totalAmount;

    private String isPfEligible;
    private String isPensionEligible;

    private Integer totalContributionMonth;
    private Integer totalNonContributionMonth;

    private BigDecimal totalPfAmount;
    private BigDecimal totalPensionAmount;
    private BigDecimal totalPfInterest;
    private BigDecimal totalPensionInterest;

    private String recommendedBenefitType;

    // ================================================================
    // EXCESS SERVICE FIELDS
    // ================================================================
    
    private BigDecimal excessOpeningBalance;
    private BigDecimal excessServiceAmount;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate excessCutoffDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate excessStartDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate excessEndDate;
    
    private BigDecimal excessTotalContributions;
    private BigDecimal excessTotalInterest;
    private Integer excessEolMonths;

    private List<ClaimRuleEvaluationListDto> ruleEvaluations;

    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}