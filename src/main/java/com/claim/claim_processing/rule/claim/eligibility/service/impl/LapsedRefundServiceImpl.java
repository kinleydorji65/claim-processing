package com.claim.claim_processing.rule.claim.eligibility.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.entities.claim.ClaimEligibilityComponentMap;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundCategoryMap;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundComponentMap;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeDetail;
import com.claim.claim_processing.common.repository.agencyRelated.AgencyCategoryRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundCategoryMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundComponentMapRepository;
import com.claim.claim_processing.common.repository.claim.ClaimLapsedRefundRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeDetailRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.CategoryBenefitsDTO;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedRefundPreviewResponseDTO;
import com.claim.claim_processing.rule.claim.eligibility.service.LapsedRefundService;
import com.claim.claim_processing.rule.claim.mapper.LapsedRefundPreviewMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LapsedRefundServiceImpl implements LapsedRefundService {

    private final ClaimLapsedRefundRepository lapsedRefundRepository;
    private final ClaimLapsedRefundCategoryMapRepository categoryMapRepository;
    private final ClaimLapsedRefundComponentMapRepository componentMapRepository;
    private final MemberContributionService memberContributionService;
    private final BenefitComponentTypeDetailRepository benefitComponentTypeDetailRepository;
    private final AgencyCategoryRepository agencyCategoryRepository;
    private final LapsedRefundPreviewMapper lapsedRefundPreviewMapper;


    @Override
    public LapsedRefundPreviewResponseDTO previewLapsedRefund(ClaimPreviewRequest request) {

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
                .filter(rule -> rule.getSchemeType().getId().equals(contributionSummary.getSchemeTypeId()))
                .filter(rule -> matchesContributionMonths(rule, totalMonths))
                .filter(rule -> matchesEffectiveDate(rule, terminationDate))
                .findFirst()
                .orElseThrow(() -> ClaimException.notFound("No matching lapsed refund rule found"));

        // 3. Get category mappings for this rule
        ClaimLapsedRefundCategoryMap categoryMapping = categoryMapRepository
                .findByRule_IdAndCategory_CategoryId(matchingRule.getId(), request.getMemberCategoryId().toString())
                .orElseThrow(() -> ClaimException.notFound("Lapsed refund not applicable for this agency category"));

        List<CategoryBenefitsDTO> categoryBenefitsList = new ArrayList<>();

        // 4. Filter by member's category
        ClaimLapsedRefundComponentMap categoryMap = componentMapRepository.findById(categoryMapping.getId())
                .orElseThrow(() -> ClaimException.notFound("Lapsed refund not applicable for this agency category"));

        // 5. Get benefit components for this rule + category map
        List<ClaimLapsedRefundComponentMap> componentMappings = componentMapRepository
                .findByRule_IdAndClaimLapsedRefundCategoryMap_IdAndIsActive(
                        matchingRule.getId(),
                        categoryMap.getId(),
                        ActivityEnum.Y);

        // 6. Convert to DTOs
        List<EligibleBenefitComponentDTO> lapsedBenefits = componentMappings.stream()
                .map(component -> {
                    String eligibleComponentType = getEligibleComponentType(component);
                    return EligibleBenefitComponentDTO.builder()
                    .benifitComponentName(component.getBenefitComponentType().getName())
                    .code(eligibleComponentType)
                    .build();
                })
                .collect(Collectors.toList());
        Map<String, List<EligibleBenefitComponentDTO>> benefitsByType = groupBenefitsByType(lapsedBenefits);
        String agencyCategory = agencyCategoryRepository.findById(request.getMemberCategoryId())
                    .orElseThrow(() -> ClaimException.notFound("Agency category not found"))
                    .getCategoryName();
        // 7. Build and return response

        categoryBenefitsList.add(CategoryBenefitsDTO.builder()
                    .ruleName(matchingRule.getRuleName())
                    .ruleCode(matchingRule.getRuleCode())
                    .agencyCategoryName(agencyCategory)
                    .allBenefits(lapsedBenefits)
                    .build());
        return lapsedRefundPreviewMapper.toResponse(
                contributionSummary,
                categoryBenefitsList,
                matchingRule
        );
    }

    private String getEligibleComponentType(ClaimLapsedRefundComponentMap component) {
        BenefitComponentTypeDetail componentDetail = benefitComponentTypeDetailRepository.findById(component.getId())
                                    .orElseThrow(() -> ClaimException.notFound("Benefit component details not found"));

        return componentDetail.getComponent().getCode();
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