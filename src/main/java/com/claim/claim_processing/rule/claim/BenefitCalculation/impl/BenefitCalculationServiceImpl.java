package com.claim.claim_processing.rule.claim.BenefitCalculation.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;

import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.claim.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimEligibilityPreviewResponse;
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

        ClaimEligibilityPreviewResponse claimEligibilityPreviewResponse = claimEligibilityRuleService
                .previewEligibility(request);
        LapsedRefundPreviewResponseDTO previewLapsedRefund = lapsedRefundService.previewLapsedRefund(request);

        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(request.getMemberCode());
        VestingRuleResponseDTO vestingResponse = vestingRuleService.determineVestingEligibility(request);

        BigDecimal serviceYears = calculateServiceYears(
                contributionSummary.getContributionStartDate(),
                contributionSummary.getContributionEndDate());
        return processComponentsWithRules(
                contributionSummary,
                vestingResponse,
                claimEligibilityPreviewResponse,
                previewLapsedRefund,
                serviceYears);
    }

    private BigDecimal calculateServiceYears(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
    }

    private Set<String> collectAllComponentCodes(
            VestingRuleResponseDTO vesting,
            ClaimEligibilityPreviewResponse eligibility,
            LapsedRefundPreviewResponseDTO lapsed) {

        Set<String> codes = new HashSet<>();

        if (vesting != null) {
            codes.addAll(extractCodes(vesting.getCategoryBenefits()));
        }

        if (eligibility != null) {
            codes.addAll(extractCodes(eligibility.getEligibleBenefits()));
        }

        if (lapsed != null) {
            codes.addAll(extractCodes(lapsed.getLapsedBenefits()));
        }

        return codes;
    }

    private List<String> extractCodes(List<EligibleBenefitComponentDTO> list) {
        if (list == null)
            return Collections.emptyList();

        return list.stream()
                .map(EligibleBenefitComponentDTO::getCode)
                .filter(Objects::nonNull)
                .toList();
    }

    private ClaimCalculationResponseDTO processComponentsWithRules(
            MemberContributionSummary contributionSummary,
            VestingRuleResponseDTO vestingResponse,
            ClaimEligibilityPreviewResponse eligibilityResponse,
            LapsedRefundPreviewResponseDTO lapsedResponse,
            BigDecimal serviceYears) {

        // 1. Collect rule component codes
        Set<String> validCodes = collectAllComponentCodes(
                vestingResponse,
                eligibilityResponse,
                lapsedResponse);

        List<MemberContributionSummary.ComponentGroup> groups = contributionSummary.getComponentGroups();

        BigDecimal totalPfAmount = groups.stream()
                .filter(g -> g.getCode().startsWith("PF_"))
                .map(MemberContributionSummary.ComponentGroup::getTotalBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPensionAmount = groups.stream()
                .filter(g -> g.getCode().startsWith("PC_"))
                .map(MemberContributionSummary.ComponentGroup::getTotalBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Filter contribution components
        List<ClaimCalculationResponseDTO.ComponentBalanceDTO> components = groups
                .stream()
                .filter(cg -> validCodes.contains(cg.getCode()))
                .map((MemberContributionSummary.ComponentGroup cg) -> ComponentBalanceDTO.builder()
                        .code(cg.getCode())
                        .name(cg.getName())
                        .type(cg.getCode().contains("I") ? "INTEREST" : "CONTRIBUTION")
                        .amount(cg.getPrincipal())
                        .rate(cg.getInterest())
                        .build())
                .toList();

        return ClaimCalculationResponseDTO.builder()
                .memberCode(contributionSummary.getMemberCode())
                .contributionStartDate(contributionSummary.getContributionStartDate())
                .contributionEndDate(contributionSummary.getContributionEndDate())
                .cessationDate(contributionSummary.getCessationDate())
                .lastInterestCalculationDate(null) // set if available
                .totalPfAmount(totalPfAmount)
                .totalPensionAmount(totalPensionAmount)
                .components(components)
                .asOfDate(contributionSummary.getBalanceAsOfDate())
                .build();
    }
}
