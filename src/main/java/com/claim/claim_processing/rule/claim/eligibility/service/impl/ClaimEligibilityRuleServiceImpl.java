package com.claim.claim_processing.rule.claim.eligibility.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.entities.claim.ClaimEligibilityCategoryMap;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityComponentMap;
import com.claim.claim_processing.common.entities.claim.ClaimEligibilityMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeDetail;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityCategoryMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityComponentMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimEligibilityRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeDetailRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
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
    private final BenefitComponentTypeDetailRepository benefitComponentTypeDetailRepository;
    private final AgencyCategoryRepository agencyCategoryRepository;
    private static final String PENSION_CODE = "PENSION";

    @Override
    public ClaimEligibilityPreviewResponse previewEligibility(ClaimPreviewRequest request) {
        System.out.println("request received in service: " + request);
        // 1. Fetch contribution summary (snapshot-based)
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(request.getMemberCode());

        Integer totalMonths = contributionSummary.getTotalContributionMonths();
        LocalDate cessationDate = request.getCessationDate() != null ? request.getCessationDate()
                : contributionSummary.getContributionEndDate();

                System.out.println("Total contribution months: " + totalMonths + ", claim circumstance: " + cessationDate);

        List<ClaimEligibilityMaster> rules =
        ruleRepository.findByIsActive(ActivityEnum.Y);

System.out.println("TOTAL RULES = " + rules.size());

for (ClaimEligibilityMaster rule : rules) {

    boolean c1 = rule.getClaimCircumstance().getId().equals(request.getCircumtancesId());
    boolean c2 = rule.getSchemeType().getId().equals(contributionSummary.getSchemeTypeId());
    boolean c3 = matchesContributionMonths(rule, totalMonths);
    boolean c4 = matchesEffectiveDate(rule, cessationDate);

    System.out.println("RULE ID: " + rule.getId());
    System.out.println("circumstance match = " + c1);
    System.out.println("scheme match        = " + c2);
    System.out.println("month match         = " + c3);
    System.out.println("date match          = " + c4);
    System.out.println("----------------------------------");
}
        ClaimEligibilityMaster matchingRule = rules.stream()
        .filter(rule -> Objects.equals(rule.getClaimCircumstance().getId(), request.getCircumtancesId()))
        .filter(rule -> Objects.equals(rule.getSchemeType().getId(), contributionSummary.getSchemeTypeId()))
        .filter(rule -> matchesContributionMonths(rule, totalMonths))
        .filter(rule -> matchesEffectiveDate(rule, cessationDate))
        .findFirst()
        .orElseThrow(() -> ClaimException.notFound("No matching eligibility rule found"));
        System.out.println("Matching rule found: " + matchingRule.getId() + " - " + matchingRule.getRuleName());
        // 3. Get category mappings for this rule
        ClaimEligibilityCategoryMap categoryMapping = categoryMapRepository
                .findByRule_IdAndCategory_CategoryId(matchingRule.getId(), request.getMemberCategoryId())
                .orElseThrow(() ->  ClaimException.notFound("No matching category mapping found"));
    System.out.println("Matching category mapping found: " + categoryMapping.getId() + " for category " + request.getMemberCategoryId());
        // 4. Build category benefits
        List<CategoryBenefitsDTO> categoryBenefitsList = new ArrayList<>();
        List<ClaimEligibilityComponentMap> componentMappings = new ArrayList<>();
        // Get benefit components for this specific rule + category map
        System.out.println("Fetching component mappings for rule " + matchingRule.getId() + " and category map " + categoryMapping.getId());
        componentMappings = componentMapRepository
                .findByRule_IdAndClaimEligibilityCategoryMap_IdAndIsActive(
                        matchingRule.getId(),
                        categoryMapping.getId(),
                            ActivityEnum.Y); 

            // Convert to DTOs
            List<EligibleBenefitComponentDTO> benefits = componentMappings.stream()
                    .map(component -> {
                        // Determine if this specific benefit component is eligible based on the rule
                        boolean isPensionEligible = PENSION_CODE.contains(component.getBenefitComponentType().getCode());

                        String eligibleComponentType = getEligibleComponentType(component);
                        System.out.println("Component ID: " + component.getId() + ", eligibleComponentType Code: " + eligibleComponentType + ", isPensionEligible: " + isPensionEligible);
                        return EligibleBenefitComponentDTO.builder()
                                .code(eligibleComponentType)
                                .benifitComponentName(component.getBenefitComponentType().getName())
                                .isPensionEligible(isPensionEligible ? EligibilityEnum.ELIGIBLE : EligibilityEnum.NOT_ELIGIBLE)
                                .selectable(isPensionEligible)
                                .build();
                    })
                    .collect(Collectors.toList());

            // Group benefits by type (Pension, PF, Full Formula)
            AgencyCategory agencyCategory = agencyCategoryRepository.findById(request.getMemberCategoryId())
                    .orElseThrow(() -> ClaimException.notFound("Agency category not found"));
                    
            categoryBenefitsList.add(CategoryBenefitsDTO.builder()
                    .ruleName(matchingRule.getRuleName())
                    .ruleCode(matchingRule.getRuleCode())
                    .agencyCategoryName(agencyCategory.getCategoryName())
                    .allBenefits(benefits)
                    .build());
                    System.out.println("categoryBenefitsList Total benefits mapped for category " + agencyCategory.getCategoryName() + ": " + benefits.size());

        // 5. Build and return response
        return previewMapper.toResponse(
                benefits,
                matchingRule
            );
    }

    private String getEligibleComponentType(ClaimEligibilityComponentMap component) {
        BenefitComponentTypeDetail componentDetail = benefitComponentTypeDetailRepository.findById(component.getId())
                                    .orElseThrow(() -> ClaimException.notFound("Benefit component details not found"));
        System.out.println("Component code for component map ID " + component.getId() + " is " + componentDetail.getComponent().getCode());
        return componentDetail.getComponent().getCode();
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