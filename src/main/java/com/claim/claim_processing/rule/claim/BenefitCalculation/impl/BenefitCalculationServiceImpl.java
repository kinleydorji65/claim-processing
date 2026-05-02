package com.claim.claim_processing.rule.claim.BenefitCalculation.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimEligibilityPreviewResponse;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedCategoryBenefitsDTO;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedRefundPreviewResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.VestingRuleResponseDTO;
import com.claim.claim_processing.rule.claim.eligibility.service.ClaimEligibilityRuleService;
import com.claim.claim_processing.rule.claim.eligibility.service.LapsedRefundService;
import com.claim.claim_processing.rule.claim.vesting.service.VestingRuleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BenefitCalculationServiceImpl implements BenefitCalculationService {
    
    private final MemberContributionService memberContributionService;
    private final ClaimEligibilityRuleService claimEligibilityRuleService;
    private final LapsedRefundService lapsedRefundService;
    private final VestingRuleService vestingRuleService;

    public ClaimCalculationResponseDTO calculateBenefit(ClaimPreviewRequest request) {

        ClaimEligibilityPreviewResponse claimEligibilityPreviewResponse = claimEligibilityRuleService.previewEligibility(request);
        LapsedRefundPreviewResponseDTO previewLapsedRefund = lapsedRefundService.previewLapsedRefund(request);

        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(request.getMemberCode());
        VestingRuleResponseDTO vestingResponse = vestingRuleService.determineVestingEligibility(request);

        BigDecimal serviceYears = calculateServiceYears(
            contributionSummary.getContributionStartDate(),
            contributionSummary.getContributionEndDate()
        );
        // 1. Fetch contribution summary for the member
        // 2. Determine eligible benefits based on contribution and category
        // 3. Calculate benefit amounts using defined formulas
        // 4. Construct and return ClaimCalculationResponseDTO with results
        
        return null; // Placeholder return
    }

    private BigDecimal calculateServiceYears(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
        return BigDecimal.ZERO;
    }
    long days = ChronoUnit.DAYS.between(startDate, endDate);
    return BigDecimal.valueOf(days)
        .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
}
    private List<ComponentBalanceDTO> processComponentsWithRules(MemberContributionSummary contributionSummary, 
                                                                LapsedRefundPreviewResponseDTO vestingResponse, 
                                                                ClaimEligibilityPreviewResponse eligibilityResponse, 
                                                                LapsedCategoryBenefitsDTO lapsedResponse, BigDecimal serviceYears) {
        
    
        
        return null;
    }
}
