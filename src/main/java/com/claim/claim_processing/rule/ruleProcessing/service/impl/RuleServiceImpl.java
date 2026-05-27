package com.claim.claim_processing.rule.ruleProcessing.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleGateWay.dto.MatchedConditionRuleDto;
import com.claim.claim_processing.rule.ruleGateWay.dto.MatchedConditionRuleDto.MatchedConditionResponse;
import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto;
import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto.ClaimRuleResponseDto.ClaimRuleConditionResponse;
import com.claim.claim_processing.rule.ruleGateWay.service.RuleGateWayService;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {
    private final RuleGateWayService ruleGateWayService;
    private final ClaimTypeRuleMapRepository claimTypeRuleMapRepository;
    private final MemberContributionService memberContributionService;
    private final CessationTypeRepository cessationTypeRepository;

    @Override
    public ApiResponseDTO<List<MatchedConditionRuleDto>> playWithRule(ClaimInitialPreviewRequest request) {

        List<ClaimTypeRuleMap> ruleMaps = claimTypeRuleMapRepository.findByClaimType_Id(request.getClaimTypeId());

        if (ruleMaps.isEmpty()) {
            throw new RuntimeException("No rules configured for claim type id: " + request.getClaimTypeId());
        }

        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(request.getNppfNumber());

        Integer totalMonths = contributionSummary.getTotalContributionMonths();

        LocalDate cessationDate = request.getCessationDate() != null
                ? request.getCessationDate()
                : contributionSummary.getContributionEndDate();

        List<MatchedConditionRuleDto> matchedRules = new ArrayList<>();

        for (ClaimTypeRuleMap map : ruleMaps) {

            RuleResponseDto ruleResponseDto = ruleGateWayService
                    .getByTopRuleType(map.getRuleType().getId())
                    .getData();

            if (ruleResponseDto == null || ruleResponseDto.getSubClaimRules() == null) {
                continue;
            }

            boolean terminationClaim = isTerminationClaim(request);

            List<RuleResponseDto.ClaimRuleResponseDto> subRules = ruleResponseDto.getSubClaimRules();

            // NORMAL CLAIM -> exclude termination rules
            if (!terminationClaim) {
                subRules = subRules.stream()
                        .filter(sr -> !isTerminationRuleType(sr))
                        .toList();
            }

            // TERMINATION CLAIM -> exclude normal rules
            else {
                subRules = subRules.stream()
                        .filter(this::isTerminationRuleType)
                        .toList();
            }

            List<MatchedConditionRuleDto> matchedSubRules = subRules.stream()
                    .filter(sr -> matchesRequestFilter(sr, request, cessationDate))
                    .map(sr -> mapToMatchedRule(sr, request, contributionSummary, totalMonths))
                    .filter(Objects::nonNull)
                    .toList();

            matchedRules.addAll(matchedSubRules);
        }

        if (matchedRules.isEmpty()) {
            return ApiResponseDTO.notFound("Rule Does Not Match");
        }

        return ApiResponseDTO.success(matchedRules);
    }

    private boolean matchesRequestFilter(
            RuleResponseDto.ClaimRuleResponseDto sr,
            ClaimInitialPreviewRequest request,
            LocalDate cessationDate) {

        boolean dateMatch = cessationDate == null
                || ((sr.getEffectiveFrom() == null || !cessationDate.isBefore(sr.getEffectiveFrom()))
                        &&
                        (sr.getEffectiveTo() == null || !cessationDate.isAfter(sr.getEffectiveTo())));

        if (!dateMatch) {
            return false;
        }

        if (sr.getLoanTypeId() != null && sr.getLoanTypeId() != 0) {
            return true;
        }

        if (request.getReasonTypeId() != null && request.getReasonTypeId() != 0) {
            return Objects.equals(sr.getPartialReasonId(), request.getReasonTypeId());
        }

        return true;
    }

    private MatchedConditionRuleDto mapToMatchedRule(
            RuleResponseDto.ClaimRuleResponseDto sr,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary,
            Integer totalMonths) {
        MatchedConditionRuleDto.MatchedConditionResponse matchedCondition = matchesCondition(sr.getClaimRuleCondition(),
                contributionSummary, totalMonths);

        if (matchedCondition == null) {
            return null;
        }

        RuleResponseDto.ClaimRuleResponseDto.AgencyCategories matchedCategory = null;

        if (sr.getClaimRuleCondition().getAgencyCategories() != null
                && request.getMemberCategoryId() != null) {

            matchedCategory = sr.getClaimRuleCondition()
                    .getAgencyCategories()
                    .stream()
                    .filter(category -> Objects.equals(
                            category.getCategoryId(),
                            request.getMemberCategoryId()))
                    .findFirst()
                    .orElse(null);
        }

        List<MatchedConditionRuleDto.Components> components = matchedCategory == null
                || matchedCategory.getComponents() == null
                        ? List.of()
                        : matchedCategory.getComponents()
                                .stream()
                                .map(component -> MatchedConditionRuleDto.Components.builder()
                                        .componentId(component.getComponentId())
                                        .componentName(component.getName())
                                        .componentCode(component.getCode())
                                        .build())
                                .toList();

        List<MatchedConditionRuleDto.RefundTypeDTO> refundTypes = matchedCategory == null
                || matchedCategory.getRefundTypes() == null
                        ? List.of()
                        : matchedCategory.getRefundTypes()
                                .stream()
                                .map(refund -> MatchedConditionRuleDto.RefundTypeDTO.builder()
                                        .id(refund.getId())
                                        .name(refund.getName())
                                        .build())
                                .toList();

        return MatchedConditionRuleDto.builder()
                .subRuleId(sr.getId())
                .ruleCode(sr.getRuleCode())
                .ruleName(sr.getRuleName())
                .loanTypeId(sr.getLoanTypeId())
                .loanType(sr.getLoanType())
                .reasonId(sr.getPartialReasonId())
                .reasonName(sr.getPartialReason())
                .effectiveFrom(sr.getEffectiveFrom())
                .effectiveTo(sr.getEffectiveTo())
                .condition(matchedCondition)
                .components(components)
                .refundTypes(refundTypes)
                .build();
    }

    private MatchedConditionResponse matchesCondition(
            ClaimRuleConditionResponse condition,
            MemberContributionSummary contributionSummary,
            Integer totalMonths) {

        if (condition == null) {
            return null;
        }

        if (!"Y".equalsIgnoreCase(condition.getIsActive())) {
            return null;
        }

        if (condition.getSchemeTypeId() != null
                && contributionSummary != null
                && contributionSummary.getSchemeTypeId() != null
                && !Objects.equals(condition.getSchemeTypeId(), contributionSummary.getSchemeTypeId())) {
            return null;
        }

        if (totalMonths == null) {
            return null;
        }

        boolean hasMin = condition.getMinMonths() != null;
        boolean hasMax = condition.getMaxMonths() != null;
        boolean hasTotal = condition.getTotalContributionNumber() != null;
        boolean hasComparison = condition.getComparisonType() != null
                && !condition.getComparisonType().isBlank();

        if (hasTotal) {
            if (!hasComparison) {
                return null;
            }

            return compare(totalMonths,
                    condition.getTotalContributionNumber().intValue(),
                    condition.getComparisonType())
                            ? buildMatchedCondition(condition)
                            : null;
        }

        if (hasComparison) {

            if (hasMin && !hasMax) {
                return compare(totalMonths,
                        condition.getMinMonths().intValue(),
                        condition.getComparisonType())
                                ? buildMatchedCondition(condition)
                                : null;
            }

            if (!hasMin && hasMax) {
                return compare(totalMonths,
                        condition.getMaxMonths().intValue(),
                        condition.getComparisonType())
                                ? buildMatchedCondition(condition)
                                : null;
            }

            if (hasMin && hasMax) {
                return totalMonths >= condition.getMinMonths().intValue()
                        && totalMonths <= condition.getMaxMonths().intValue()
                                ? buildMatchedCondition(condition)
                                : null;
            }
        }

        if (hasMin && totalMonths < condition.getMinMonths().intValue()) {
            return null;
        }

        if (hasMax && totalMonths > condition.getMaxMonths().intValue()) {
            return null;
        }

        return buildMatchedCondition(condition);
    }

    private boolean isTerminationClaim(ClaimInitialPreviewRequest request) {
        if (request.getCessationTypeId() == null) {
            return false;
        }

        CessationTypeMaster cessationType = cessationTypeRepository.findById(request.getCessationTypeId())
                .orElseThrow(() -> new RuntimeException("Cessation type not found"));

        return cessationType.getCode() != null
                && cessationType.getCode().toUpperCase().contains("TERMINATION");
    }

    private MatchedConditionResponse buildMatchedCondition(ClaimRuleConditionResponse condition) {
        return MatchedConditionResponse.builder()
                .id(condition.getId())
                .schemeTypeName(condition.getSchemeTypeName())
                .schemeTypeId(condition.getSchemeTypeId())
                .priorityOrder(condition.getPriorityOrder())
                .totalContributionNumber(condition.getTotalContributionNumber())
                .withdrawalPercentage(condition.getWithdrawalPercentage())
                .minMonths(condition.getMinMonths())
                .maxMonths(condition.getMaxMonths())
                .comparisonType(condition.getComparisonType())
                .isActive(condition.getIsActive())
                .accumulation(condition.getAccumulation())
                .build();
    }

    private boolean isTerminationRuleType(RuleResponseDto.ClaimRuleResponseDto sr) {
        if (sr == null) {
            return false;
        }

        // Check both ruleCode and ruleName
        String code = sr.getRuleCode() != null ? sr.getRuleCode().toUpperCase() : "";
        String name = sr.getRuleName() != null ? sr.getRuleName().toUpperCase() : "";

        return code.contains("TERM") ||
                code.contains("TERMINATION") ||
                name.contains("TERM") ||
                name.contains("TERMINATION");
    }

    private boolean compare(
            Integer actualValue,
            Integer ruleValue,
            String comparisonType) {
        if (actualValue == null || ruleValue == null || comparisonType == null) {
            return false;
        }

        return switch (comparisonType) {
            case "GREATER_THAN" -> actualValue > ruleValue;
            case "GREATER_THAN_OR_EQUAL" -> actualValue >= ruleValue;
            case "LESS_THAN" -> actualValue < ruleValue;
            case "LESS_THAN_OR_EQUAL" -> actualValue <= ruleValue;
            case "EQUAL" -> actualValue.equals(ruleValue);
            default -> false;
        };
    }
}
