package com.claim.claim_processing.rule.ruleProcessing.service.impl;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.claim.CessationTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.repository.claim.CessationTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.member.service.MemberService;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleProcessing.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.CategorySchemeMapping;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.ClaimComponentExpressionMapping;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.ClaimComponentMapping;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimCondition;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimMapping;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimTimeIndication;
import com.claim.claim_processing.rule.ruleProcessing.repositories.RefundTypeRepository;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.CategorySchemeMappingRepository;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.SubClaimConditionRepository;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.SubClaimMappingRepository;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private static final Long PARTIAL_WITHDRAWAL_CLAIM_TYPE_ID = 2L;
    private static final Long WRONG_REMITTANCE_CLAIM_TYPE_ID = 5L;

    private final ClaimTypeRuleMapRepository claimTypeRuleMapRepository;
    private final SubClaimMappingRepository subClaimMappingRepository;
    private final SubClaimConditionRepository subClaimConditionRepository;
    private final CategorySchemeMappingRepository categorySchemeMappingRepository;
    private final CessationTypeRepository cessationTypeRepository;
    private final MemberService memberService;
    private final MemberContributionService memberContributionService;
    private final RefundTypeRepository refundTypeRepository;

    @Override
public ApiResponseDTO<List<MatchedSubClaimRuleDto>> playWithRule(
        ClaimInitialPreviewRequest request) {

    try {
        validateRequest(request);

        MemberDetailResponseDto memberDetail = getMemberDetail(request.getNppfNumber());
        MemberContributionSummary contributionSummary = getContributionSummary(request.getNppfNumber());

        String memberCategoryId = memberDetail.getMemberCategoryId();
        Long schemeTypeId = contributionSummary.getSchemeTypeId();

        LocalDate cessationDate = resolveEndDate(request, contributionSummary);
        LocalDate serviceStartDate = toLocalDate(memberDetail.getDateOfServiceJoiningDate());

        Integer totalServiceMonths = calculateMonths(serviceStartDate, cessationDate);
        Integer totalServiceYears = totalServiceMonths == null ? null : totalServiceMonths / 12;

        Integer totalContributionMonths = contributionSummary.getTotalContributionMonths();
        Integer totalContributionYears = contributionSummary.getTotalContributionYears();
        Integer totalNonContributionMonths = contributionSummary.getTotalNonContributionMonths();

        boolean partialClaim = isPartialWithdrawalClaim(request.getClaimTypeId());
        boolean terminationClaim = isTerminationClaim(request.getCessationTypeId());

        List<String> ruleTypeCodes = getClaimTypeRuleMaps(request.getClaimTypeId())
                .stream()
                .filter(Objects::nonNull)
                .map(ClaimTypeRuleMap::getRuleType)
                .filter(Objects::nonNull)
                .map(RuleTypeMaster::getCode)
                .filter(Objects::nonNull)
                .toList();

        List<SubClaimMapping> subClaimMappings = getSubClaimMappings(ruleTypeCodes);

        CategorySchemeMapping normalCategorySchemeMapping =
                getCategorySchemeMapping(schemeTypeId, memberCategoryId);

        CategorySchemeMapping vestingCategorySchemeMapping =
                getVestingCategorySchemeMapping(memberCategoryId);

        List<SubClaimMapping> reasonOrTerminationFiltered;

        if (partialClaim) {
            reasonOrTerminationFiltered = subClaimMappings.stream()
                    .filter(Objects::nonNull)
                    .filter(mapping -> Objects.equals(
                            mapping.getPartialReasonId(),
                            request.getReasonTypeId()))
                    .toList();
        } else {
            reasonOrTerminationFiltered = subClaimMappings.stream()
                    .filter(Objects::nonNull)
                    .filter(mapping -> {

                        if (isVestingRule(mapping)) {
                            return true;
                        }

                        return terminationClaim
                                ? isTerminationRule(mapping)
                                : !isTerminationRule(mapping);
                    })
                    .toList();
        }

        List<SubClaimMapping> categoryFiltered = reasonOrTerminationFiltered.stream()
                .filter(mapping -> {

                    if (isVestingRule(mapping)) {
                        return matchesCategoryScheme(
                                mapping,
                                vestingCategorySchemeMapping
                        );
                    }

                    return matchesCategoryScheme(
                            mapping,
                            normalCategorySchemeMapping
                    );
                })
                .toList();

        List<SubClaimMapping> timeFiltered = categoryFiltered.stream()
                .filter(mapping -> matchesTimeIndication(
                        mapping.getTimeIndication(),
                        cessationDate))
                .toList();

        List<SubClaimMapping> matchedMappings = timeFiltered.stream()
                .filter(mapping -> partialClaim
                        ? matchesPartialMappingCondition(
                                mapping,
                                cessationDate,
                                totalContributionMonths)
                        : matchesMappingCondition(
                                mapping,
                                cessationDate,
                                totalContributionMonths,
                                totalContributionYears,
                                totalNonContributionMonths,
                                totalServiceMonths,
                                totalServiceYears))
                .toList();

        if (matchedMappings.isEmpty()) {
            return ApiResponseDTO.notFound("No matching rule found");
        }

        List<MatchedSubClaimRuleDto> response = matchedMappings.stream()
                .map(mapping -> mapToMatchedSubClaimRuleDto(
                        mapping,
                        partialClaim,
                        cessationDate,
                        totalContributionMonths,
                        totalContributionYears,
                        totalNonContributionMonths,
                        totalServiceMonths,
                        totalServiceYears))
                .toList();

        return ApiResponseDTO.success(response);

    } catch (ClaimException ex) {
        ex.printStackTrace();
        return ApiResponseDTO.notFound(ex.getMessage());

    } catch (Exception ex) {
        ex.printStackTrace();
        throw ClaimException.internalError(
                "An unexpected error occurred: " + ex.getMessage());
    }
}

    private boolean isVestingRule(SubClaimMapping mapping) {

        if (mapping == null) {
            return false;
        }
        System.out.println("Checking if mapping is vesting rule for mapping: " + mapping.getRuleType().getCode());
        String ruleType = mapping.getRuleType() == null
                ? ""
                : mapping.getRuleType().getCode().trim().toUpperCase();

        String subClaimType = mapping.getRuleType() == null
                ? ""
                : mapping.getRuleType().getCode().trim().toUpperCase();

        return ruleType.contains("VEST")
                || subClaimType.contains("VEST");
    }

    private boolean matchesPartialMappingCondition(
            SubClaimMapping mapping,
            LocalDate cessationDate,
            Integer totalContributionMonths) {

        if (mapping == null || mapping.getSubClaimCode() == null) {
            return false;
        }

        List<SubClaimCondition> conditions = subClaimConditionRepository.findBySubClaimMapping_SubClaimCode(
                mapping.getSubClaimCode());

        if (conditions == null || conditions.isEmpty()) {
            return false;
        }

        return conditions.stream()
                .filter(Objects::nonNull)
                .filter(condition -> matchesEffectiveDate(condition, cessationDate))
                .anyMatch(condition -> matchesCondition(
                        condition,
                        cessationDate,
                        totalContributionMonths,
                        null,
                        null,
                        null,
                        null));
    }

    private boolean matchesMappingCondition(
            SubClaimMapping mapping,
            LocalDate cessationDate,
            Integer totalContributionMonths,
            Integer totalContributionYears,
            Integer totalNonContributionMonths,
            Integer totalServiceMonths,
            Integer totalServiceYears) {

        if (mapping == null || mapping.getId() == null) {
            return false;
        }

        List<SubClaimCondition> conditions = subClaimConditionRepository
                .findBySubClaimMapping_SubClaimCode(mapping.getSubClaimCode());
        System.out.println("CHECKING CONDITIONS FOR RULE: "
            + mapping.getSubClaimCode()
            + " | "
            + (mapping.getRuleType() == null ? "" : mapping.getRuleType().getCode())
            + " | condition count = "
            + (conditions == null ? 0 : conditions.size()));
        return conditions.stream()
                .anyMatch(condition -> matchesCondition(
                        condition,
                        cessationDate,
                        totalContributionMonths,
                        totalContributionYears,
                        totalNonContributionMonths,
                        totalServiceMonths,
                        totalServiceYears));
    }

    private boolean matchesTimeIndication(
            SubClaimTimeIndication timeIndication,
            LocalDate cessationDate) {

        if (timeIndication == null) {
            return true;
        }

        if (cessationDate == null) {
            return false;
        }

        String indication = timeIndication.getTimeIndication();

        if (indication == null || indication.isBlank()) {
            return true;
        }

        LocalDate startDate = timeIndication.getStartDate();
        LocalDate endDate = timeIndication.getEndDate();

        return switch (indication.trim().toUpperCase()) {
            case "AFTER" -> startDate != null && !cessationDate.isBefore(startDate);
            case "BEFORE" -> endDate != null && !cessationDate.isAfter(endDate);
            case "BETWEEN" -> startDate != null && endDate != null
                    && !cessationDate.isBefore(startDate)
                    && !cessationDate.isAfter(endDate);
            case "ANY" -> true;
            default -> false;
        };
    }

    private boolean matchesCondition(
            SubClaimCondition condition,
            LocalDate endDate,
            Integer totalContributionMonths,
            Integer totalContributionYears,
            Integer totalNonContributionMonths,
            Integer totalServiceMonths,
            Integer totalServiceYears) {

        if (condition == null) {
            return false;
        }

        System.out.println("CONDITION DEBUG => "
            + "code=" + condition.getConditionCode()
            + ", check=" + condition.getConditionCheck()
            + ", expression=" + condition.getExpression()
            + ", duration=" + condition.getDuration()
            + ", endDate=" + endDate
            + ", totalContributionMonths=" + totalContributionMonths
            + ", totalContributionYears=" + totalContributionYears
            + ", totalServiceMonths=" + totalServiceMonths
            + ", totalServiceYears=" + totalServiceYears);

        if (!matchesEffectiveDate(condition, endDate)) {
            return false;
        }

        String conditionCheck = condition.getConditionCheck();
        String expression = condition.getExpression();
        Long duration = condition.getDuration();

        if (conditionCheck == null || expression == null || duration == null) {
            return false;
        }

        Long actualValue = getActualValue(
                conditionCheck,
                totalContributionMonths,
                totalContributionYears,
                totalNonContributionMonths,
                totalServiceMonths,
                totalServiceYears);

        if (actualValue == null) {
            return false;
        }

        return evaluateExpression(actualValue, duration, expression);
    }

    private Long getActualValue(
        String conditionCheck,
        Integer totalContributionMonths,
        Integer totalContributionYears,
        Integer totalNonContributionMonths,
        Integer totalServiceMonths,
        Integer totalServiceYears) {

    if (conditionCheck == null) {
        return null;
    }

    switch (conditionCheck.trim().toUpperCase()) {

        case "TOTAL_CONTRIBUTION_MONTHS":
        case "CONTRIBUTION_MONTHS_BEFORE_CUTOFF":
            return totalContributionMonths == null
                    ? null
                    : totalContributionMonths.longValue();

        case "TOTAL_CONTRIBUTION_YEARS":
            return totalContributionYears == null
                    ? null
                    : totalContributionYears.longValue();

        case "TOTAL_NON_CONTRIBUTION_MONTHS":
            return totalNonContributionMonths == null
                    ? null
                    : totalNonContributionMonths.longValue();

        case "TOTAL_SERVICE_MONTHS":
            return totalServiceMonths == null
                    ? null
                    : totalServiceMonths.longValue();

        case "TOTAL_SERVICE_YEARS":
            return totalServiceYears == null
                    ? null
                    : totalServiceYears.longValue();

        default:
            return null;
    }
}

    private boolean evaluateExpression(
            Long actualValue,
            Long ruleValue,
            String expression) {

        if (actualValue == null || ruleValue == null || expression == null) {
            return false;
        }

        return switch (expression.trim().toUpperCase()) {

            case "GREATER_THAN" -> actualValue > ruleValue;

            case "GREATER_THAN_EQUAL" -> actualValue >= ruleValue;

            case "LESS_THAN" -> actualValue < ruleValue;

            case "LESS_THAN_EQUAL" -> actualValue <= ruleValue;

            case "EQUAL" -> actualValue.equals(ruleValue);

            case "NOT_EQUAL" -> !actualValue.equals(ruleValue);

            default -> false;
        };
    }

    private boolean matchesEffectiveDate(
            SubClaimCondition condition,
            LocalDate endDate) {

        if (endDate == null) {
            return true;
        }

        if (condition.getEffectiveFrom() != null
                && endDate.isBefore(condition.getEffectiveFrom())) {
            return false;
        }

        if (condition.getEffectiveTo() != null
                && endDate.isAfter(condition.getEffectiveTo())) {
            return false;
        }

        return true;
    }

    private void validateRequest(ClaimInitialPreviewRequest request) {

        if (request == null) {
            throw ClaimException.notFound("Request cannot be null");
        }

        if (request.getClaimTypeId() == null) {
            throw ClaimException.notFound("Claim type is required");
        }

        if (request.getNppfNumber() == null || request.getNppfNumber().isBlank()) {
            throw ClaimException.notFound("NPPF number is required");
        }
    }

    private MemberDetailResponseDto getMemberDetail(String nppfNumber) {

        ApiResponseDTO<MemberDetailResponseDto> response = memberService.getMemberDetails(nppfNumber);

        if (response == null || response.getData() == null) {
            throw ClaimException.notFound(
                    "Member detail not found for nppfNumber: " + nppfNumber);
        }

        return response.getData();
    }

    private MemberContributionSummary getContributionSummary(String nppfNumber) {

        MemberContributionSummary summary = memberContributionService.getContributionSummary(nppfNumber);

        if (summary == null) {
            throw ClaimException.notFound(
                    "Contribution detail not found for nppfNumber: " + nppfNumber);
        }

        return summary;
    }

    private List<ClaimTypeRuleMap> getClaimTypeRuleMaps(Long claimTypeId) {

        List<ClaimTypeRuleMap> ruleMaps = claimTypeRuleMapRepository.findByClaimType_Id(claimTypeId);

        if (ruleMaps == null || ruleMaps.isEmpty()) {
            throw ClaimException.notFound(
                    "No rules configured for claim type id: " + claimTypeId);
        }

        return ruleMaps;
    }

    private List<SubClaimMapping> getSubClaimMappings(List<String> ruleTypeCodes) {

        if (ruleTypeCodes == null || ruleTypeCodes.isEmpty()) {
            return List.of();
        }

        return ruleTypeCodes.stream()
                .map(subClaimMappingRepository::findByRuleType_CodeIgnoreCase)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .toList();
    }

    private CategorySchemeMapping getCategorySchemeMapping(
        Long schemeTypeId,
        String memberCategoryId) {

    return categorySchemeMappingRepository
            .findBySchemeType_IdAndAgencyCategory_CategoryId(
                    schemeTypeId,
                    memberCategoryId)
            .orElse(null);
}

private CategorySchemeMapping getVestingCategorySchemeMapping(
        String memberCategoryId) {

    return categorySchemeMappingRepository
            .findByAgencyCategory_CategoryIdAndSchemeTypeIsNull(
                    memberCategoryId)
            .orElse(null);
}

    private LocalDate resolveEndDate(
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary) {

        if (request.getCessationDate() != null) {
            return request.getCessationDate();
        }

        if (contributionSummary.getContributionEndDate() != null) {
            return contributionSummary.getContributionEndDate();
        }

        return LocalDate.now();
    }

    private LocalDate toLocalDate(Date date) {

        if (date == null) {
            return null;
        }

        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate(); // ← This works!
        }

        // For java.util.Date
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private Integer calculateMonths(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            return null;
        }

        return Math.toIntExact(
                ChronoUnit.MONTHS.between(startDate, endDate));
    }

    private boolean isPartialWithdrawalClaim(Long claimTypeId) {
        return Objects.equals(claimTypeId, PARTIAL_WITHDRAWAL_CLAIM_TYPE_ID);
    }

    private boolean isWrongRemittanceClaim(Long claimTypeId) {
        return Objects.equals(claimTypeId, WRONG_REMITTANCE_CLAIM_TYPE_ID);
    }

    private boolean isTerminationClaim(Long cessationTypeId) {

        if (cessationTypeId == null || cessationTypeId <= 0) {
            return false;
        }
        CessationTypeMaster cessationType = cessationTypeRepository.findById(cessationTypeId)
                .orElseThrow(() -> ClaimException.notFound(null));

        String code = cessationType.getCode();

        return code != null && code.equalsIgnoreCase("TERMINATION");
    }

    private boolean isTerminationRule(SubClaimMapping mapping) {

        if (mapping == null) {
            return false;
        }

        String code = mapping.getRuleType() == null
                ? ""
                : mapping.getRuleType().getCode().toUpperCase();

        String name = mapping.getRuleType() == null
                ? ""
                : mapping.getRuleType().getName().toUpperCase();

        return code.contains("TERM")
                || code.contains("TERMINATION")
                || name.contains("TERM")
                || name.contains("TERMINATION");
    }

    private MatchedSubClaimRuleDto mapToMatchedSubClaimRuleDto(
            SubClaimMapping mapping,
            boolean partialClaim,
            LocalDate cessationDate,
            Integer totalContributionMonths,
            Integer totalContributionYears,
            Integer totalNonContributionMonths,
            Integer totalServiceMonths,
            Integer totalServiceYears) {

        if (mapping == null) {
            return null;
        }

        return MatchedSubClaimRuleDto.builder()
                .subClaimMappingId(mapping.getId())
                .subClaimCode(mapping.getSubClaimCode())
                .subClaimType(mapping.getSubClaimType())
                .subClaimDescription(mapping.getSubClaimDesc())
                .ruleCode(mapping.getRuleType() != null ? mapping.getRuleType().getCode() : null)
                .ruleName(mapping.getRuleType() != null ? mapping.getRuleType().getName() : null)
                .refundTypeName(getRefundName(mapping.getRefundTypeId()))
                .isRefundEligible((mapping.getRefundTypeId() != null && mapping.getRefundTypeId() > 0) ? true : false)
                // For partial, this is your percentage: SB63 = 50
                .withdrawalPercentage(mapping.getWithdrawalPercentage())
                .effectiveFrom(mapping.getEffectiveFrom())
                .effectiveTo(mapping.getEffectiveTo())
                .categoryScheme(mapCategoryScheme(mapping.getCategorySchemeMapping()))
                .condition(partialClaim
                        ? mapMatchedPartialCondition(
                                mapping,
                                cessationDate,
                                totalContributionMonths)
                        : mapMatchedCondition(
                                mapping,
                                cessationDate,
                                totalContributionMonths,
                                totalContributionYears,
                                totalNonContributionMonths,
                                totalServiceMonths,
                                totalServiceYears))

                .timeIndication(mapTimeIndication(mapping.getTimeIndication()))
                .componentMapping(mapComponentMapping(mapping.getComponentMapping()))
                .build();
    }

    private String getRefundName(Long refundTypeId) {
        if (refundTypeId == null || refundTypeId <= 0) {
            return null;
        }
        return refundTypeRepository.findById(refundTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refund type ID: " + refundTypeId))
                .getName();
    }

    private MatchedSubClaimRuleDto.Condition mapMatchedPartialCondition(
            SubClaimMapping mapping,
            LocalDate cessationDate,
            Integer totalContributionMonths) {

        if (mapping == null || mapping.getSubClaimCode() == null) {
            return null;
        }

        List<SubClaimCondition> conditions = subClaimConditionRepository.findBySubClaimMapping_SubClaimCode(
                mapping.getSubClaimCode());

        if (conditions == null || conditions.isEmpty()) {
            return null;
        }

        return conditions.stream()
                .filter(Objects::nonNull)
                .filter(condition -> matchesEffectiveDate(condition, cessationDate))
                .filter(condition -> matchesCondition(
                        condition,
                        cessationDate,
                        totalContributionMonths,
                        null,
                        null,
                        null,
                        null))
                .findFirst()
                .map(this::mapCondition)
                .orElse(null);
    }

    private MatchedSubClaimRuleDto.Condition mapMatchedCondition(
            SubClaimMapping mapping,
            LocalDate cessationDate,
            Integer totalContributionMonths,
            Integer totalContributionYears,
            Integer totalNonContributionMonths,
            Integer totalServiceMonths,
            Integer totalServiceYears) {

        if (mapping == null || mapping.getSubClaimCode() == null) {
            return null;
        }

        List<SubClaimCondition> conditions = subClaimConditionRepository.findBySubClaimMapping_SubClaimCode(
                mapping.getSubClaimCode());

        if (conditions == null || conditions.isEmpty()) {
            return null;
        }

        return conditions.stream()
                .filter(Objects::nonNull)
                .filter(condition -> matchesCondition(
                        condition,
                        cessationDate,
                        totalContributionMonths,
                        totalContributionYears,
                        totalNonContributionMonths,
                        totalServiceMonths,
                        totalServiceYears))
                .findFirst()
                .map(this::mapCondition)
                .orElse(null);
    }

    private MatchedSubClaimRuleDto.Condition mapCondition(
            SubClaimCondition condition) {

        if (condition == null) {
            return null;
        }

        return MatchedSubClaimRuleDto.Condition.builder()
                .id(condition.getId())
                .conditionCode(condition.getConditionCode())
                .conditionCheck(condition.getConditionCheck())
                .expression(condition.getExpression())
                .duration(condition.getDuration())
                .effectiveFrom(condition.getEffectiveFrom())
                .effectiveTo(condition.getEffectiveTo())
                .build();
    }

    private boolean matchesCategoryScheme(
        SubClaimMapping mapping,
        CategorySchemeMapping categorySchemeMapping) {

    if (mapping == null
            || mapping.getCategorySchemeMapping() == null
            || categorySchemeMapping == null) {
        return false;
    }

    return Objects.equals(
            mapping.getCategorySchemeMapping().getCategorySchemeCode(),
            categorySchemeMapping.getCategorySchemeCode()
    );
}

    private MatchedSubClaimRuleDto.CategoryScheme mapCategoryScheme(CategorySchemeMapping categorySchemeMapping) {

        if (categorySchemeMapping == null) {
            return null;
        }

        return MatchedSubClaimRuleDto.CategoryScheme.builder()
                .categoryId(categorySchemeMapping.getId())
                .categorySchemeCode(categorySchemeMapping.getCategorySchemeCode())
                .categoryCode(categorySchemeMapping.getCategoryCode())
                .schemeCode(categorySchemeMapping.getSchemeCode())
                .categoryName(categorySchemeMapping.getAgencyCategory() != null ? categorySchemeMapping.getAgencyCategory().getCategoryName() : null)
                .schemeTypeId(categorySchemeMapping.getSchemeType() != null ? categorySchemeMapping.getSchemeType().getId() : null)
                .schemeTypeName(categorySchemeMapping.getSchemeType() != null ? categorySchemeMapping.getSchemeType().getName() : null)
                .build();
    }

    private MatchedSubClaimRuleDto.ComponentMapping mapComponentMapping(
            ClaimComponentMapping componentMapping) {

        if (componentMapping == null) {
            return null;
        }

        return MatchedSubClaimRuleDto.ComponentMapping.builder()
                .id(componentMapping.getId())
                .componentMappingCode(componentMapping.getComponentMappingCode())

                .hasPfMc(toYN(componentMapping.getHasPfMc()))
                .hasPfEc(toYN(componentMapping.getHasPfEc()))
                .hasPfImc(toYN(componentMapping.getHasPfImc()))
                .hasPfIec(toYN(componentMapping.getHasPfIec()))

                .hasPMc(toYN(componentMapping.getHasPMc()))
                .hasPEc(toYN(componentMapping.getHasPEc()))
                .hasPImc(toYN(componentMapping.getHasPImc()))
                .hasPIec(toYN(componentMapping.getHasPIec()))

                .hasGc(toYN(componentMapping.getHasGc()))
                .hasGic(toYN(componentMapping.getHasGic()))
                .hasVc(toYN(componentMapping.getHasVc()))
                .hasVic(toYN(componentMapping.getHasVic()))
                .hasIvc(toYN(componentMapping.getHasIvc()))
                .hasIgc(toYN(componentMapping.getHasIgc()))

                .expressions(mapComponentExpressions(componentMapping.getExpressions()))
                .effectiveFrom(componentMapping.getEffectiveFrom())
                .effectiveTo(componentMapping.getEffectiveTo())
                .build();
    }

    private String toYN(String value) {
        return "Y".equalsIgnoreCase(value) ? "Y" : "N";
    }

    private List<MatchedSubClaimRuleDto.ComponentExpression> mapComponentExpressions(
            List<ClaimComponentExpressionMapping> expressions) {

        if (expressions == null || expressions.isEmpty()) {
            return List.of();
        }

        return expressions.stream()
                .filter(Objects::nonNull)
                .map(expression -> MatchedSubClaimRuleDto.ComponentExpression.builder()
                        .id(expression.getId())
                        .componentMappingCode(expression.getComponentMapping().getComponentMappingCode())
                        .expression(expression.getExpression())
                        .build())
                .toList();
    }

    private MatchedSubClaimRuleDto.TimeIndication mapTimeIndication(
            SubClaimTimeIndication timeIndication) {

        if (timeIndication == null) {
            return null;
        }

        return MatchedSubClaimRuleDto.TimeIndication.builder()
                .id(timeIndication.getId())
                .timeIndicationCode(timeIndication.getTimeIndicationCode())
                .timeIndication(timeIndication.getTimeIndication())
                .startDate(timeIndication.getStartDate())
                .endDate(timeIndication.getEndDate())
                .effectiveFrom(timeIndication.getEffectiveFrom())
                .effectiveTo(timeIndication.getEffectiveTo())
                .build();
    }
}