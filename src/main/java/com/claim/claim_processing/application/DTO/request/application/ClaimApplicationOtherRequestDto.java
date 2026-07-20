package com.claim.claim_processing.application.DTO.request.application;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationOtherRequestDto {
    private Integer totalContributionMonths;
    private Integer totalNonContributionMonths;
    private String recommendedBenefitType;
    private BigDecimal totalPfAmount;
    private BigDecimal totalPensionAmount;
    private BigDecimal totalAmount;
    
    private BigDecimal totalPensionInterest;
    private BigDecimal totalPfInterest;

    private EligibilityEnum pfIsEligible;
    private EligibilityEnum pensionIsEligible;
    private BigDecimal finalPayableAmount;
}
