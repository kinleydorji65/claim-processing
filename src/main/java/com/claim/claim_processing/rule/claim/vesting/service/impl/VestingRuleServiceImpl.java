package com.claim.claim_processing.rule.claim.vesting.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.entities.claim.ClaimVestingRuleMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.repository.claim.ClaimVestingRuleMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
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

        // Log all rules being considered
        log.info("Finding matching rule for Category: {}, Total Months: {}, Cessation Date: {}",
                memberCategoryId, totalMonths, cessationDate);

        return activeRules.stream()
                // Filter by member category
                .filter(rule -> rule.getCategory().getCategoryId().equals(memberCategoryId))
                // Filter by effective date range
                .filter(rule -> matchesEffectiveDate(rule, cessationDate))
                // Filter by vesting months with cutoff logic
                .filter(rule -> matchesVestingMonths(rule, totalMonths, contributionStartDate))
                .sorted(Comparator.comparing((ClaimVestingRuleMaster r) -> r.getCutoff() == null ? 1 : 0))
                // Find first matching rule
                .findFirst()
                .orElseThrow(() -> ClaimException.notFound("No matching vesting rule found for the given criteria"));
    }

    private boolean matchesEffectiveDate(ClaimVestingRuleMaster rule, LocalDate cessationDate) {
        if (cessationDate == null)
            return true;

        LocalDate effectiveFrom = rule.getEffectiveFrom();
        LocalDate effectiveTo = rule.getEffectiveTo();

        if (effectiveFrom != null && cessationDate.isBefore(effectiveFrom)) {
            log.debug("Rule {} rejected: Cessation date {} is before effective from {}",
                    rule.getRuleCode(), cessationDate, effectiveFrom);
            return false;
        }
        if (effectiveTo != null && cessationDate.isAfter(effectiveTo)) {
            log.debug("Rule {} rejected: Cessation date {} is after effective to {}",
                    rule.getRuleCode(), cessationDate, effectiveTo);
            return false;
        }
        return true;
    }

    private boolean matchesVestingMonths(ClaimVestingRuleMaster rule,
            Integer totalMonths,
            LocalDate contributionStartDate) {

        // SPECIAL HANDLING: For rules with cutoff (Grandfather rules)
        if (rule.getCutoff() != null) {
            log.info("Rule {} has cutoff - checking grandfather condition", rule.getRuleCode());

            LocalDate cutoffDate = rule.getCutoff().getCutoffDate();
            Integer requiredMonthsBeforeCutoff = rule.getCutoff().getRequiredMonths();

            // Calculate months contributed BEFORE the cutoff date
            Integer monthsBeforeCutoff = calculateMonthsBetween(contributionStartDate, cutoffDate);
            boolean attainedRequiredBeforeCutoff = monthsBeforeCutoff >= requiredMonthsBeforeCutoff;

            log.info("Rule {} - Months before cutoff: {}, Required: {}, Attained: {}",
                    rule.getRuleCode(), monthsBeforeCutoff, requiredMonthsBeforeCutoff, attainedRequiredBeforeCutoff);

            // For grandfather rule to apply, member MUST have attained required months
            // BEFORE cutoff
            if (!attainedRequiredBeforeCutoff) {
                log.info("Rule {} rejected: Member did not attain {} months before {}",
                        rule.getRuleCode(), requiredMonthsBeforeCutoff, cutoffDate);
                return false;
            }

            // If cutoff condition passed, then check the range condition
            // For CIVIL_GRANDFATHER_240: total months between 240-275
            Integer minMonths = rule.getMinVestingMonths();
            Integer maxMonths = rule.getMaxVestingMonths();

            boolean matchesRange = totalMonths >= minMonths && totalMonths <= maxMonths;
            log.info("Rule {} - Total months: {}, Range: {}-{}, Matches: {}",
                    rule.getRuleCode(), totalMonths, minMonths, maxMonths, matchesRange);

            return matchesRange;
        }

        // NORMAL HANDLING: For rules without cutoff
        String comparisonType = rule.getComparisonType();
        Integer minMonths = rule.getMinVestingMonths();
        Integer maxMonths = rule.getMaxVestingMonths();

        log.debug("Rule {} - Comparison: {}, Total Months: {}, Min: {}, Max: {}",
                rule.getRuleCode(), comparisonType, totalMonths, minMonths, maxMonths);

        switch (comparisonType) {
            case "LT":
                return totalMonths < minMonths;
            case "GTE":
                return totalMonths >= minMonths;
            case "RANGE":
                if (minMonths != null && maxMonths != null) {
                    return totalMonths >= minMonths && totalMonths <= maxMonths;
                } else if (minMonths != null) {
                    return totalMonths >= minMonths;
                } else if (maxMonths != null) {
                    return totalMonths <= maxMonths;
                }
                return false;
            default:
                log.warn("Unknown comparison type: {}", comparisonType);
                return false;
        }
    }

    private VestingRuleResponseDTO buildResponse(ClaimVestingRuleMaster rule,
            Integer totalMonths,
            Integer totalServiceMonths) {

        if (rule == null) {
            return VestingRuleResponseDTO.builder()
                    .eligible(false)
                    .totalVestingMonths(totalMonths)
                    .message("No matching vesting rule found")
                    .build();
        }

        // Determine available options based on refund type and payout result
        List<String> availableOptions = getAvailableOptions(rule);

        // Determine vesting status
        String vestingStatus = determineVestingStatus(rule, totalMonths);

        return VestingRuleResponseDTO.builder()
                .eligible(true)
                .ruleCode(rule.getRuleCode())
                .ruleName(getRuleDisplayName(rule))
                .refundType(rule.getRefundType())
                .payoutResult(rule.getPayoutResult())
                .vestingStatus(vestingStatus)
                .totalVestingMonths(totalMonths)
                .requiredVestingMonths(rule.getMinVestingMonths())
                .message(getMessage(rule))
                .remarks(rule.getRemarks())
                .availableOptions(availableOptions)
                .build();
    }

    private List<String> getAvailableOptions(ClaimVestingRuleMaster rule) {
        // For grandfather rules (with cutoff) - only Pension
        if (rule.getCutoff() != null) {
            return List.of("PENSION");
        }

        // Based on refund type
        switch (rule.getRefundType()) {
            case "OPTION":
                return List.of("PENSION", "LUMPSUM");
            case "LUMPSUM":
                return List.of("LUMPSUM");
            case "PENSION":
                return List.of("PENSION");
            default:
                return List.of();
        }
    }

    private String determineVestingStatus(ClaimVestingRuleMaster rule, Integer totalMonths) {
        // Grandfathered rules
        if (rule.getCutoff() != null) {
            return "GRANDFATHERED";
        }

        String comparisonType = rule.getComparisonType();
        Integer minMonths = rule.getMinVestingMonths();
        Integer maxMonths = rule.getMaxVestingMonths();

        switch (comparisonType) {
            case "GTE":
                if (totalMonths >= minMonths) {
                    return "FULLY_VESTED";
                }
                break;
            case "LT":
                if (totalMonths < minMonths) {
                    return "PARTIALLY_VESTED";
                }
                break;
            case "RANGE":
                if (totalMonths >= minMonths && totalMonths <= maxMonths) {
                    return "PARTIALLY_VESTED";
                } else if (totalMonths > maxMonths) {
                    return "FULLY_VESTED";
                }
                break;
        }

        return "NOT_VESTED";
    }

    private String getMessage(ClaimVestingRuleMaster rule) {
        // Grandfather rules
        if (rule.getCutoff() != null) {
            return "Grandfathered member - Eligible for pension only (no lumpsum option)";
        }

        // Based on refund type
        switch (rule.getRefundType()) {
            case "OPTION":
                return "You can choose between Pension or Lumpsum";
            case "LUMPSUM":
                return "You are eligible for lumpsum refund only";
            case "PENSION":
                return "You are eligible for pension only";
            default:
                return "Eligible for benefits as per vesting rules";
        }
    }

    private String getRuleDisplayName(ClaimVestingRuleMaster rule) {
        // Customize display name based on rule code
        switch (rule.getRuleCode()) {
            // CIVIL RULES
            case "CIVIL_BEFORE_2019_LT_120":
                return "Civil (Before Jul 2019) - Less than 10 years";
            case "CIVIL_BEFORE_2019_GTE_120":
                return "Civil (Before Jul 2019) - 10 years or more";
            case "CIVIL_2019_2024_LT_240":
                return "Civil (Jul 2019 - Oct 2024) - Less than 20 years";
            case "CIVIL_2019_2024_GTE_240":
                return "Civil (Jul 2019 - Oct 2024) - 20 years or more";
            case "CIVIL_AFTER_2024_LT_276":
                return "Civil (After Nov 2024) - Less than 23 years";
            case "CIVIL_AFTER_2024_GTE_276":
                return "Civil (After Nov 2024) - 23 years or more";
            case "CIVIL_GRANDFATHER_240":
                return "Civil Grandfather Rule (Pension Only)";

            // AF RULES
            case "AF_BEFORE_2022_LT_120":
                return "AF (Before Dec 2022) - Less than 10 years";
            case "AF_BEFORE_2022_GTE_120":
                return "AF (Before Dec 2022) - 10 years or more";
            case "AF_AFTER_2022_LT_240":
                return "AF (After Dec 2022) - Less than 20 years";
            case "AF_AFTER_2022_GTE_240":
                return "AF (After Dec 2022) - 20 years or more";

            default:
                return rule.getRuleCode().replace("_", " ");
        }
    }

    private Integer calculateMonthsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null)
            return 0;
        if (endDate.isBefore(startDate))
            return 0;
        return (int) ChronoUnit.MONTHS.between(startDate, endDate);
    }
}