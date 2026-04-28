package com.claim.claim_processing.rule.claim.eligibility.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.entities.claim.ClaimEligibilityCategoryMap;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityComponentMap;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityCategoryMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityComponentMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityRepository;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimEligibilityPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.CategoryBenefitsDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimEligibilityPreviewResponse;
import com.claim.claim_processing.rule.claim.eligibility.service.ClaimEligibilityRuleService;
import com.claim.claim_processing.rule.claim.mapper.ClaimEligibilityPreviewMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimEligibilityRuleServiceImpl implements ClaimEligibilityRuleService {

    private final ClaimEligibilityRepository ruleRepository;
    private final ClaimEligibilityCategoryMapRepository categoryMapRepository;
    private final ClaimEligibilityComponentMapRepository componentMapRepository;
    private final ClaimEligibilityPreviewMapper previewMapper;
    private final MemberContributionService memberContributionService;

    @Override
    public ClaimEligibilityPreviewResponse previewEligibility(ClaimEligibilityPreviewRequest request) {

        // 1. Fetch contribution summary (snapshot-based)
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(request.getMemberCode());

        Integer totalMonths = contributionSummary.getTotalContributionMonths();
        LocalDate terminationDate = request.getTerminationDate() != null ? request.getTerminationDate()
                : contributionSummary.getContributionEndDate();

        // 2. Find matching rule
        ClaimEligibilityMaster matchingRule = ruleRepository.findByIsActive(ActivityEnum.Y)
                .stream()
                .filter(rule -> rule.getClaimCircumstance().getId().equals(request.getCircumtancesId()))
                .filter(rule -> matchesSchemeType(rule, contributionSummary.getSchemeTypeId()))
                .filter(rule -> matchesContributionMonths(rule, totalMonths))
                .filter(rule -> matchesEffectiveDate(rule, terminationDate))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No matching eligibility rule found"));

        // 3. Get category mappings for this rule
        List<ClaimEligibilityCategoryMap> categoryMappings = categoryMapRepository
                .findByRule_Id(matchingRule.getId(), ActivityEnum.Y);

        // 4. Build category benefits
        List<CategoryBenefitsDTO> categoryBenefitsList = new ArrayList<>();
        List<ClaimEligibilityComponentMap> componentMappings = new ArrayList<>();
        for (ClaimEligibilityCategoryMap categoryMap : categoryMappings) {
            // Get benefit components for this specific rule + category map
            componentMappings = componentMapRepository
                    .findByRule_IdAndClaimEligibilityCategoryMap_IdAndIsActive(
                            matchingRule.getId(),
                            categoryMap.getId(),
                            ActivityEnum.Y);

            // Convert to DTOs
            List<EligibleBenefitComponentDTO> benefits = componentMappings.stream()
                    .map(component -> {
                        // Determine if this specific benefit component is eligible based on the rule
                        boolean isEligible = determineBenefitEligibility(component, matchingRule, contributionSummary,
                                request);

                        String ineligibilityReason = null;
                        if (!isEligible) {
                            ineligibilityReason = getIneligibilityReason(component, matchingRule, contributionSummary,
                                    request);
                        }

                        return EligibleBenefitComponentDTO.builder()
                                .benefitComponentTypeId(component.getBenefitComponentType().getId())
                                .code(component.getBenefitComponentType().getCode())
                                .name(component.getBenefitComponentType().getName())
                                .isEligible(isEligible ? EligibilityEnum.ELIGIBLE : EligibilityEnum.NOT_ELIGIBLE)
                                .selectable(isEligible)
                                .ineligibilityReason(ineligibilityReason)
                                .build();
                    })
                    .collect(Collectors.toList());

            // Group benefits by type (Pension, PF, Full Formula)
            Map<String, List<EligibleBenefitComponentDTO>> benefitsByType = groupBenefitsByType(benefits);

            categoryBenefitsList.add(CategoryBenefitsDTO.builder()
                    .agencyCategoryId(categoryMap.getCategory().getCategoryId())
                    .agencyCategoryName(categoryMap.getCategory().getCategoryName())
                    .allBenefits(benefits)
                    .pensionBenefits(benefitsByType.getOrDefault("PENSION", new ArrayList<>()))
                    .pfBenefits(benefitsByType.getOrDefault("PF", new ArrayList<>()))
                    .fullFormulaBenefits(benefitsByType.getOrDefault("FULL_FORMULA", new ArrayList<>()))
                    .build());
        }

        // 5. Build and return response
        return previewMapper.toResponse(
                contributionSummary,
                matchingRule,
                componentMappings);
    }

    private boolean determineBenefitEligibility(
            ClaimEligibilityComponentMap component,
            ClaimEligibilityMaster matchingRule,
            MemberContributionSummary contributionSummary,
            ClaimEligibilityPreviewRequest request) {

        String benefitCode = component.getBenefitComponentType().getCode();

        // Check based on benefit type and rule conditions
        switch (matchingRule.getRuleCode()) {
            case "TERM_BEFORE_2013_GT_12":
                // Only PF benefits are eligible, not Pension
                if (benefitCode.startsWith("PENSION")) {
                    return false;
                }
                break;

            case "TERM_AFTER_2013_GT_12_TIER2":
                // Tier 2 rules: Pension is NA
                if (benefitCode.startsWith("PENSION")) {
                    return false;
                }
                break;

            case "NORMAL_GT_12_TIER2":
                // Tier 2 normal: Only PF benefits, no Pension
                if (benefitCode.startsWith("PENSION")) {
                    return false;
                }
                break;

            case "NORMAL_LT_12_TIER2":
                // Tier 2 normal less than 12 months: Only PF benefits
                if (benefitCode.startsWith("PENSION")) {
                    return false;
                }
                break;

            case "PRIVATE_LT_12_TERM":
                // Private rules: No Pension
                if (benefitCode.startsWith("PENSION")) {
                    return false;
                }
                break;

            default:
                // For simultaneous rules, both PF and Pension are eligible
                break;
        }

        // Additional checks based on contribution months
        Integer totalMonths = contributionSummary.getTotalContributionMonths();

        // Check if benefit requires minimum months
        if (benefitCode.contains("WITH_INTEREST") && totalMonths < 12) {
            return false; // Interest benefits may require minimum 12 months
        }

        return true;
    }

    private String getIneligibilityReason(
            ClaimEligibilityComponentMap component,
            ClaimEligibilityMaster matchingRule,
            MemberContributionSummary contributionSummary,
            ClaimEligibilityPreviewRequest request) {

        String benefitCode = component.getBenefitComponentType().getCode();

        if (benefitCode.startsWith("PENSION")) {
            return "Pension benefits are not eligible for " + matchingRule.getRuleName();
        }

        if (benefitCode.contains("WITH_INTEREST") && contributionSummary.getTotalContributionMonths() < 12) {
            return "Interest benefits require minimum 12 months of contribution";
        }

        return "Not eligible based on contribution rules";
    }

    private Map<String, List<EligibleBenefitComponentDTO>> groupBenefitsByType(
            List<EligibleBenefitComponentDTO> benefits) {

        return benefits.stream()
                .collect(Collectors.groupingBy(benefit -> {
                    String code = benefit.getCode();

                    if (code.startsWith("PENSION")) {
                        return "PENSION";
                    } else if (code.startsWith("PF")) {
                        return "PF";
                    } else {
                        return "FULL_FORMULA";
                    }
                }));
    }

    private boolean matchesSchemeType(ClaimEligibilityMaster rule, Long schemeTypeId) {
        if (rule.getSchemeType() != null && rule.getSchemeType().getId() != null) {
            return rule.getSchemeType().getId().equals(schemeTypeId);
        }
        return true;
    }

    private boolean matchesContributionMonths(ClaimEligibilityMaster rule, Integer months) {
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

    private boolean matchesEffectiveDate(ClaimEligibilityMaster rule, LocalDate terminationDate) {
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