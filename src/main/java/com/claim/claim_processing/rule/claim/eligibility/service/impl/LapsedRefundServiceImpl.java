package com.claim.claim_processing.rule.claim.eligibility.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundCategoryMap;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundComponentMap;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundCategoryMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundComponentMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.request.EligibilityPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedRefundPreviewResponseDTO;
import com.claim.claim_processing.rule.claim.eligibility.service.LapsedRefundService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LapsedRefundServiceImpl implements LapsedRefundService {

    private final ClaimLapsedRefundRepository lapsedRefundRepository;
    private final ClaimLapsedRefundCategoryMapRepository categoryMapRepository;
    private final ClaimLapsedRefundComponentMapRepository componentMapRepository;
    private final MemberContributionService memberContributionService;

    @Override
    public LapsedRefundPreviewResponseDTO previewLapsedRefund(EligibilityPreviewRequest request) {

        // 1. Fetch contribution summary (snapshot-based)
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(request.getMemberCode());

        Integer totalMonths = contributionSummary.getTotalContributionMonths();
        LocalDate terminationDate = request.getTerminationDate() != null ? request.getTerminationDate()
                : contributionSummary.getContributionEndDate();

        // 2. Find matching rule
        ClaimLapsedRefundMaster matchingRule = lapsedRefundRepository.findByIsActive(ActivityEnum.Y)
                .stream()
                .filter(rule -> rule.getClaimCircumstance().getId().equals(request.getCircumtancesId()))
                .filter(rule -> matchesSchemeType(rule, contributionSummary.getSchemeTypeId()))
                .filter(rule -> matchesContributionMonths(rule, totalMonths))
                .filter(rule -> matchesEffectiveDate(rule, terminationDate))
                .findFirst()
                .orElseThrow(() -> ClaimException.notFound("No matching lapsed refund rule found"));

        // 3. Get category mappings for this rule
        List<ClaimLapsedRefundCategoryMap> categoryMappings = categoryMapRepository
                .findByRule_Id(matchingRule.getId());

        // 4. Filter by member's category
        ClaimLapsedRefundCategoryMap categoryMap = categoryMappings.stream()
                .filter(map -> map.getCategory().getCategoryId().equals(request.getMemberCategoryId().toString()))
                .findFirst()
                .orElseThrow(() -> ClaimException.notFound("Lapsed refund not applicable for this agency category"));

        // 5. Get benefit components for this rule + category map
        List<ClaimLapsedRefundComponentMap> componentMappings = componentMapRepository
                .findByRule_IdAndClaimLapsedRefundCategoryMap_IdAndIsActive(
                        matchingRule.getId(),
                        categoryMap.getId(),
                        ActivityEnum.Y);

        // 6. Convert to DTOs
        List<EligibleBenefitComponentDTO> lapsedBenefits = componentMappings.stream()
                .map(component -> EligibleBenefitComponentDTO.builder()
                        .benefitComponentTypeId(component.getBenefitComponentType().getId())
                        .code(component.getBenefitComponentType().getCode())
                        .name(component.getBenefitComponentType().getName())
                        .isEligible(EligibilityEnum.ELIGIBLE)
                        .selectable(true)
                        .ineligibilityReason(null)
                        .build())
                .collect(Collectors.toList());

        // 7. Build and return response
        return LapsedRefundPreviewResponseDTO.builder()
                .eligible(true)
                .matchingRuleCode(matchingRule.getRuleCode())
                .matchingRuleName(matchingRule.getRuleName())
                .contributionSummary(contributionSummary)
                .lapsedBenefits(lapsedBenefits)
                .remarks(matchingRule.getRemarks())
                .build();
    }

    private boolean matchesSchemeType(ClaimLapsedRefundMaster rule, Long schemeTypeId) {
        if (rule.getSchemeType() != null && rule.getSchemeType().getId() != null) {
            return rule.getSchemeType().getId().equals(schemeTypeId);
        }
        return true;
    }

    private boolean matchesContributionMonths(ClaimLapsedRefundMaster rule, Integer months) {
        if (months == null)
            return false;

        if (rule.getMinContributionMonths() != null &&
                months < rule.getMinContributionMonths()) {
            return false;
        }

        if (rule.getMaxContributionMonths() != null &&
                months > rule.getMaxContributionMonths()) {
            return false;
        }

        return true;
    }

    private boolean matchesEffectiveDate(ClaimLapsedRefundMaster rule, LocalDate terminationDate) {
        if (terminationDate == null)
            return true;

        if (rule.getEffectiveFrom() != null &&
                terminationDate.isBefore(rule.getEffectiveFrom())) {
            return false;
        }

        if (rule.getEffectiveTo() != null &&
                terminationDate.isAfter(rule.getEffectiveTo())) {
            return false;
        }

        return true;
    }
}