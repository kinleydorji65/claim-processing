package com.claim.claim_processing.application.DTO.request.application;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import com.claim.claim_processing.application.DTO.request.detail.WrongRemitanceRequestDTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationCalculationSummaryRequest {
    private LocalDate calculationEffectiveDate;
    private BigDecimal finalPayableAmount;
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
    private String createdBy;
    private Timestamp createdAt;
    private String updatedBy;
    private Timestamp updatedAt;

    private BigDecimal excessOpeningBalance;
    private BigDecimal excessServiceAmount;
    private LocalDate excessCutoffDate;
    private LocalDate excessStartDate;
    private LocalDate excessEndDate;
    private BigDecimal excessTotalContributions;
    private BigDecimal excessTotalInterest;
    private Integer excessEolMonths;

    private List<ClaimApplicationRuleEvaluationRequestDto> ruleEvaluations;
    private List<ClaimApplicationForfeitedComponentRequestDto> forFeitedComponents;
    private ClaimApplicationDeductionRequestDto deductionDetail;
    private List<WrongRemitanceRequestDTO> wrongRemitanceRequestDTOs;
}
