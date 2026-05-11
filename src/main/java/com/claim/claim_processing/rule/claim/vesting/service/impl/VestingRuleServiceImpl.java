package com.claim.claim_processing.rule.claim.vesting.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.claim.VestingRefundTypeResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimVestingRuleMaster;
import com.claim.claim_processing.common.entities.claim.VestingRefundBenefitMap;
import com.claim.claim_processing.common.entities.claim.VestingRefundType;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeDetail;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import com.claim.claim_processing.common.repository.claim.ClaimVestingRuleMasterRepository;
import com.claim.claim_processing.common.repository.claim.VestingRefundBenefitMapRepository;
import com.claim.claim_processing.common.repository.claim.VestingRefundTypeRepository;
import com.claim.claim_processing.common.repository.contribution.BenefitComponentTypeDetailRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.VestingRuleResponseDTO;
import com.claim.claim_processing.rule.claim.vesting.service.VestingRuleService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VestingRuleServiceImpl implements VestingRuleService {

    private final MemberContributionService memberContributionService;
    private final ClaimVestingRuleMasterRepository vestingRuleRepository;
    private final VestingRefundTypeRepository vestingRefundTypeRepository;
    private final VestingRefundBenefitMapRepository vestingRefundBenefitMapRepository;
    private final BenefitComponentTypeDetailRepository benefitComponentTypeDetailRepository;

    @Override
    public VestingRuleResponseDTO determineVestingEligibility(ClaimPreviewRequest request) {

        // 1. Get contribution summary
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(request.getMemberCode());

        if (contributionSummary.getContributionEndDate() == null) {
            throw ClaimException.notFound("Member Contribution not found with Member Code: " + request.getMemberCode());
        }

        // 2. Extract data
        Integer totalContributionMonths = contributionSummary.getTotalContributionMonths();
        LocalDate contributionStartDate = contributionSummary.getContributionStartDate();
        LocalDate serviceJoiningDate = request.getServiceJoiningDate();
        LocalDate cessationDate = request.getCessationDate();

        // 3. Calculate total service months
        Integer totalServiceMonths = calculateMonthsBetween(serviceJoiningDate, cessationDate);

        // 4. Find matching rule
        ClaimVestingRuleMaster matchingRule = findMatchingRule(
                request.getMemberCategoryId(),
                cessationDate,
                totalContributionMonths,
                contributionStartDate);

        // 5. Return response
        return buildResponse(matchingRule, totalContributionMonths, totalServiceMonths);
    }

    private ClaimVestingRuleMaster findMatchingRule(String memberCategoryId,
            LocalDate cessationDate,
            Integer totalMonths,
            LocalDate contributionStartDate) {

        List<ClaimVestingRuleMaster> activeRules = vestingRuleRepository.findByIsActive(ActivityEnum.Y);

        return activeRules.stream()
                .filter(rule -> rule.getCategory().getCategoryId().equals(memberCategoryId))
                .filter(rule -> matchesEffectiveDate(rule, cessationDate))
                .filter(rule -> matchesVestingMonths(rule, totalMonths))
                .filter(rule -> ActivityEnum.Y.equals(rule.getIsActive()))
                .sorted(Comparator
                        .comparing((ClaimVestingRuleMaster r) -> Optional.ofNullable(r.getMinVestingMonths()).orElse(0))
                        .reversed()
                        .thenComparing(ClaimVestingRuleMaster::getRuleCode)) // tie-breaker
                .findFirst()
                .orElseThrow(() -> ClaimException.notFound("No matching vesting rule found"));
    }

    private boolean matchesEffectiveDate(ClaimVestingRuleMaster rule, LocalDate cessationDate) {
        if (cessationDate == null)
            return true;

        LocalDate effectiveFrom = rule.getEffectiveFrom();
        LocalDate effectiveTo = rule.getEffectiveTo();

        if (effectiveFrom != null && cessationDate.isBefore(effectiveFrom)) {
            return false;
        }
        if (effectiveTo != null && cessationDate.isAfter(effectiveTo)) {
            log.debug("Rule {} rejected: Cessation date {} is after effective to {}",
                    rule.getRuleCode(), cessationDate, effectiveTo);
            return false;
        }
        return true;
    }

    private boolean matchesVestingMonths(ClaimVestingRuleMaster rule, Integer totalMonths) {

        if (totalMonths == null) {
            return false;
        }

        String comparisonType = rule.getComparisonType();
        Integer min = rule.getMinVestingMonths();
        Integer max = rule.getMaxVestingMonths();

        log.debug("Rule {} - Comparison: {}, Total: {}, Min: {}, Max: {}",
                rule.getRuleCode(), comparisonType, totalMonths, min, max);

        switch (comparisonType) {

            case "LESS_THAN":
                return max != null && totalMonths < max;

            case "LESS_THAN_OR_EQUAL":
                return max != null && totalMonths <= max;

            case "GREATER_THAN":
                return min != null && totalMonths > min;

            case "GREATER_THAN_OR_EQUAL":
                return min != null && totalMonths >= min;

            case "RANGE":
                return (min == null || totalMonths >= min) &&
                        (max == null || totalMonths <= max);

            default:
                log.warn("Unknown comparison type: {}", comparisonType);
                return false;
        }
    }

    private VestingRuleResponseDTO buildResponse(ClaimVestingRuleMaster rule,
            Integer totalMonths,
            Integer totalServiceMonths) {

        if (rule == null) {
            return null;
        }

        return VestingRuleResponseDTO.builder()
                .ruleCode(rule.getRuleCode())
                .refundType(getRefundType(rule))
                .payoutResult(rule.getPayoutResult())
                .payoutResult(rule.getPayoutResult())
                .totalVestingMonths(totalMonths)
                .requiredVestingMonths(
                        rule.getMinVestingMonths() == null ? rule.getMaxVestingMonths() : rule.getMinVestingMonths())
                .eligibilityNote(buildEligibilityNote(rule, totalMonths))
                .categoryBenefits(getCategoryBenefits(rule))
                .build();
    }

    private List<VestingRefundTypeResponseDto> getRefundType(ClaimVestingRuleMaster rule) {

        // CASE 1: OPTION → return all refund types
        if ("OPTION".equals(rule.getPayoutResult())) {

            return vestingRefundTypeRepository.findAll()
                    .stream()
                    .map(refundType -> VestingRefundTypeResponseDto.builder()
                            .id(refundType.getId())
                            .code(refundType.getCode())
                            .name(refundType.getName())
                            .build())
                    .toList();
        }

        // CASE 2: NORMAL → return by ID

        VestingRefundType refundType = vestingRefundTypeRepository.findById(rule.getRefundType().getId())
                .orElseThrow(() -> ClaimException.notFound("Refund type not found: " + rule.getRefundType().getId()));

        return List.of(
                VestingRefundTypeResponseDto.builder()
                        .id(refundType.getId())
                        .code(refundType.getCode())
                        .name(refundType.getName())
                        .build());

    }

    private List<EligibleBenefitComponentDTO> getCategoryBenefits(ClaimVestingRuleMaster rule) {

    Long refundId = rule.getRefundType().getId();

    // 1. Get Benefit mappings for this refund type
    List<VestingRefundBenefitMap> mappings =
            vestingRefundBenefitMapRepository.findByVestingRefundType_Id(refundId);

    return mappings.stream()
            .map(map -> {

                BenefitComponentTypeMaster benefit = map.getBenefitComponentType();

                // 2. Get components under this benefit type
                List<ComponentMaster> components =
                        benefitComponentTypeDetailRepository
                                .findByBenefitComponentType_Id(benefit.getId())
                                .stream()
                                .map(BenefitComponentTypeDetail::getComponent)
                                .toList();

                // 3. Build DTO per component
                return components.stream()
                        .map(component -> EligibleBenefitComponentDTO.builder()
                                .code(component.getCode())
                                .benifitComponentName(component.getName())
                                .isPensionEligible(null) // map later if needed
                                .selectable(true)
                                .build()
                        )
                        .toList();

            })
            .flatMap(List::stream)
            .toList();
}

    private Integer calculateMonthsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null)
            return 0;
        if (endDate.isBefore(startDate))
            return 0;
        return (int) ChronoUnit.MONTHS.between(startDate, endDate);
    }

    private String buildEligibilityNote(ClaimVestingRuleMaster rule, Integer totalMonths) {

    if (rule == null) {
        return "No vesting rule found for your contribution period.";
    }

    String conditionText = buildConditionText(rule);
    String payoutText = buildPayoutText(rule.getRefundType().getCode());

    return "Based on your contribution of " + totalMonths + " months, "
            + conditionText + " " + payoutText;
}

private String buildConditionText(ClaimVestingRuleMaster rule) {

    Integer min = rule.getMinVestingMonths();
    Integer max = rule.getMaxVestingMonths();

    return switch (rule.getComparisonType()) {

        case "LESS_THAN" ->
                "your contribution is less than " + max + " months.";

        case "LESS_THAN_OR_EQUAL" ->
                "your contribution is less than or equal to " + max + " months.";

        case "GREATER_THAN" ->
                "your contribution is greater than " + min + " months.";

        case "GREATER_THAN_OR_EQUAL" ->
                "your contribution is greater than or equal to " + min + " months.";

        case "RANGE" -> {
            if (min != null && max != null) {
                yield "your contribution is between " + min + " and " + max + " months.";
            } else {
                yield "your contribution meets the required range.";
            }
        }

        default ->
                "your contribution meets the vesting requirement.";
    };
}
private String buildPayoutText(String payoutResult) {

    return switch (payoutResult) {

        case "PENSION" ->
                "You are eligible for pension benefit only.";

        case "LUMPSUM" ->
                "You are eligible for lump sum benefit only.";

        case "OPTION" ->
                "You are eligible for both pension and lump sum (option available).";

        default ->
                "Benefit eligibility could not be determined.";
    };
}
}