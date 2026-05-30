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
import com.claim.claim_processing.rule.ruleGateWay.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleGateWay.entities.rule.CategorySchemeMapping;
import com.claim.claim_processing.rule.ruleGateWay.entities.rule.ClaimComponentExpressionMapping;
import com.claim.claim_processing.rule.ruleGateWay.entities.rule.ClaimComponentMapping;
import com.claim.claim_processing.rule.ruleGateWay.entities.rule.SubClaimCondition;
import com.claim.claim_processing.rule.ruleGateWay.entities.rule.SubClaimMapping;
import com.claim.claim_processing.rule.ruleGateWay.entities.rule.SubClaimTimeIndication;
import com.claim.claim_processing.rule.ruleGateWay.repositories.rule.CategorySchemeMappingRepository;
import com.claim.claim_processing.rule.ruleGateWay.repositories.rule.SubClaimConditionRepository;
import com.claim.claim_processing.rule.ruleGateWay.repositories.rule.SubClaimMappingRepository;
import com.claim.claim_processing.rule.ruleGateWay.repositories.rule.SubClaimTimeIndicationRepository;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final SubClaimTimeIndicationRepository subClaimTimeIndicationRepository;

    @Override
    public ApiResponseDTO<List<MatchedSubClaimRuleDto>> playWithRule(
            ClaimInitialPreviewRequest request) {

        validateRequest(request);

        if (isPartialWithdrawalClaim(request.getClaimTypeId())) {
            return ApiResponseDTO.notFound("Partial Withdrawal does not use this rule flow");
        }

        if (isWrongRemittanceClaim(request.getClaimTypeId())) {
            return ApiResponseDTO.notFound("Wrong Remittance does not use this rule flow");
        }

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

        List<String> ruleTypeCodes = getClaimTypeRuleMaps(request.getClaimTypeId())
                .stream()
                .filter(Objects::nonNull)
                .map(ClaimTypeRuleMap::getRuleType)
                .filter(Objects::nonNull)
                .map(RuleTypeMaster::getCode)
                .filter(Objects::nonNull)
                .toList();

        List<SubClaimMapping> subClaimMappings = getSubClaimMappings(ruleTypeCodes);

        System.out.println("\n===== ALL RULES =====");
        subClaimMappings.forEach(rule -> System.out.println(
                rule.getId()
                        + " | "
                        + rule.getSubClaimCode()
                        + " | "
                        + rule.getSubClaimType()));
        boolean terminationClaim = isTerminationClaim(request.getCessationTypeId());

        CategorySchemeMapping categorySchemeMapping = getCategorySchemeMapping(schemeTypeId, memberCategoryId);

        List<SubClaimMapping> terminationFiltered = subClaimMappings.stream()
                .filter(Objects::nonNull)
                .filter(mapping -> terminationClaim
                        ? isTerminationRule(mapping)
                        : !isTerminationRule(mapping))
                .toList();

        System.out.println("\n===== AFTER TERMINATION FILTER =====");
        terminationFiltered.forEach(rule -> System.out.println(
                rule.getId()
                        + " | "
                        + rule.getSubClaimCode()
                        + " | "
                        + rule.getSubClaimType()));

        List<SubClaimMapping> categoryFiltered = terminationFiltered.stream()
                .filter(mapping -> matchesCategoryScheme(
                        mapping,
                        categorySchemeMapping))
                .toList();

        System.out.println("\n===== AFTER CATEGORY FILTER =====");
        categoryFiltered.forEach(rule -> System.out.println(
                rule.getId()
                        + " | "
                        + rule.getSubClaimCode()
                        + " | "
                        + rule.getSubClaimType()));

        List<SubClaimMapping> timeFiltered = categoryFiltered.stream()
                .filter(mapping -> matchesTimeIndication(
                        mapping.getTimeIndication(),
                        cessationDate))
                .toList();
        System.out.println("\n===== AFTER TIME FILTER =====");
        timeFiltered.forEach(rule -> System.out.println(
                rule.getId()
                        + " | "
                        + rule.getSubClaimCode()
                        + " | "
                        + rule.getSubClaimType()
                        + " | "
                        + (rule.getTimeIndication() != null
                                ? rule.getTimeIndication().getTimeIndicationCode()
                                : "NO_TIME_INDICATION")));
        List<SubClaimMapping> matchedMappings = timeFiltered.stream()
                .filter(mapping -> {

                    boolean matched = matchesMappingCondition(
                            mapping,
                            cessationDate,
                            totalContributionMonths,
                            totalContributionYears,
                            totalNonContributionMonths,
                            totalServiceMonths,
                            totalServiceYears);

                    System.out.println(
                            "CONDITION => "
                                    + mapping.getSubClaimCode()
                                    + " = "
                                    + matched);

                    return matched;
                })
                .toList();

        System.out.println("\n===== FINAL MATCHED RULES =====");
        matchedMappings.forEach(rule -> System.out.println(
                rule.getId()
                        + " | "
                        + rule.getSubClaimCode()
                        + " | "
                        + rule.getSubClaimType()));

        if (matchedMappings.isEmpty()) {
            return ApiResponseDTO.notFound("No matching rule found");
        }

        List<MatchedSubClaimRuleDto> response = matchedMappings.stream()
                .map(this::mapToMatchedSubClaimRuleDto)
                .toList();

        return ApiResponseDTO.success(response);
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

        return switch (conditionCheck.trim().toUpperCase()) {

            case "TOTAL_CONTRIBUTION_MONTHS" ->
                totalContributionMonths == null ? null : totalContributionMonths.longValue();

            case "TOTAL_CONTRIBUTION_YEARS" ->
                totalContributionYears == null ? null : totalContributionYears.longValue();

            case "TOTAL_NON_CONTRIBUTION_MONTHS" ->
                totalNonContributionMonths == null ? null : totalNonContributionMonths.longValue();

            case "TOTAL_SERVICE_MONTHS" ->
                totalServiceMonths == null ? null : totalServiceMonths.longValue();

            case "TOTAL_SERVICE_YEARS" ->
                totalServiceYears == null ? null : totalServiceYears.longValue();

            default -> null;
        };
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
                .orElseThrow(() -> ClaimException.notFound(
                        "No category scheme mapping found for schemeTypeId: "
                                + schemeTypeId
                                + " and memberCategoryId: "
                                + memberCategoryId));
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

        if (cessationTypeId == null) {
            return false;
        }

        CessationTypeMaster cessationType = cessationTypeRepository.findById(cessationTypeId)
                .orElseThrow(() -> ClaimException.notFound(
                        "Cessation type not found with id: " + cessationTypeId));

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
            SubClaimMapping mapping) {

        if (mapping == null) {
            return null;
        }

        return MatchedSubClaimRuleDto.builder()
                .subClaimMappingId(mapping.getId())
                .subClaimCode(mapping.getSubClaimCode())
                .subClaimType(mapping.getSubClaimType())
                .subClaimDescription(mapping.getSubClaimDesc())
                .ruleCode(mapping.getRuleType() != null
                        ? mapping.getRuleType().getCode()
                        : null)
                .ruleName(mapping.getRuleType() != null
                        ? mapping.getRuleType().getName()
                        : null)
                .withdrawalPercentage(mapping.getWithdrawalPercentage())
                .effectiveFrom(mapping.getEffectiveFrom())
                .effectiveTo(mapping.getEffectiveTo())

                .categoryScheme(mapCategoryScheme(
                        mapping.getCategorySchemeMapping()))

                // .condition(mapCondition(
                // mapping.()))
                .timeIndication(mapTimeIndication(
                        mapping.getTimeIndication()))

                .componentMapping(mapComponentMapping(
                        mapping.getComponentMapping()))

                .build();
    }

    private boolean matchesCategoryScheme(
            SubClaimMapping mapping,
            CategorySchemeMapping categorySchemeMapping) {

        if (mapping == null || categorySchemeMapping == null) {
            return false;
        }

        if (mapping.getCategorySchemeMapping() == null) {
            return false;
        }

        return Objects.equals(
                mapping.getCategorySchemeMapping().getCategorySchemeCode(),
                categorySchemeMapping.getCategorySchemeCode());
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
                .categoryName(categorySchemeMapping.getAgencyCategory().getCategoryName())
                .schemeTypeId(categorySchemeMapping.getSchemeType().getId())
                .schemeTypeName(categorySchemeMapping.getSchemeType().getName())
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
                .hasPf(componentMapping.getHasPf())
                .hasPc(componentMapping.getHasPc())
                .hasEc(componentMapping.getHasEc())
                .hasMc(componentMapping.getHasMc())
                .hasImc(componentMapping.getHasImc())
                .hasIec(componentMapping.getHasIec())
                .hasGc(componentMapping.getHasGc())
                .hasGic(componentMapping.getHasGic())
                .hasVc(componentMapping.getHasVc())
                .hasVic(componentMapping.getHasVic())
                .expressions(mapComponentExpressions(componentMapping.getExpressions()))
                .effectiveFrom(componentMapping.getEffectiveFrom())
                .effectiveTo(componentMapping.getEffectiveTo())
                .build();
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

    // private ApiResponseDTO<List<MatchedSubClaimRuleDto>> playWithPartialRule(
    // ClaimInitialPreviewRequest request) {

    // validateRequest(request);

    // MemberDetailResponseDto memberDetail =
    // getMemberDetail(request.getNppfNumber());

    // MemberContributionSummary contributionSummary =
    // getContributionSummary(request.getNppfNumber());

    // String memberCategoryId = memberDetail.getMemberCategoryId();
    // Long schemeTypeId = contributionSummary.getSchemeTypeId();

    // Integer totalContributionMonths =
    // contributionSummary.getTotalContributionMonths();

    // LocalDate effectiveDate = LocalDate.now();

    // List<ClaimTypeRuleMap> claimTypeRuleMaps =
    // getClaimTypeRuleMaps(request.getClaimTypeId());

    // List<String> ruleTypeCodes = claimTypeRuleMaps.stream()
    // .filter(Objects::nonNull)
    // .map(ClaimTypeRuleMap::getRuleType)
    // .filter(Objects::nonNull)
    // .map(RuleTypeMaster::getCode)
    // .filter(Objects::nonNull)
    // .toList();

    // List<SubClaimMapping> subClaimMappings = getSubClaimMappings(ruleTypeCodes);

    // subClaimMappings = filterByPartialWithdrawalReason(
    // subClaimMappings,
    // request.getReasonTypeId());

    // CategorySchemeMapping categorySchemeMapping =
    // getCategorySchemeMapping(schemeTypeId, memberCategoryId);

    // subClaimMappings = filterByCategoryScheme(
    // subClaimMappings,
    // categorySchemeMapping);

    // subClaimMappings = filterByEffectiveDate(
    // subClaimMappings,
    // effectiveDate);

    // subClaimMappings = filterByMinContribution(
    // subClaimMappings,
    // totalContributionMonths);

    // if (subClaimMappings.isEmpty()) {
    // return ApiResponseDTO.notFound("No matching partial withdrawal rule found");
    // }

    // List<MatchedSubClaimRuleDto> response = subClaimMappings.stream()
    // .map(this::mapPartialRule)
    // .toList();

    // return ApiResponseDTO.success(response);
    // }

    // private MatchedSubClaimRuleDto mapPartialRule(SubClaimMapping mapping) {

    // return MatchedSubClaimRuleDto.builder()
    // .subClaimMappingId(mapping.getId())
    // .subClaimCode(mapping.getSubClaimCode())
    // .subClaimType(mapping.getSubClaimType())
    // .subClaimDescription(mapping.getSubClaimDesc())
    // .ruleCode(mapping.getRuleType() != null ? mapping.getRuleType().getCode() :
    // null)
    // .ruleName(mapping.getRuleType() != null ? mapping.getRuleType().getName() :
    // null)
    // .withdrawalPercentage(mapping.getWithdrawalPercentage())
    // // .condition(mapCondition(details.condition()))
    // .categoryScheme(mapCategoryScheme(mapping.getCategorySchemeMapping()))
    // .componentMapping(mapComponentMapping(mapping.getComponentMapping()))
    // .effectiveFrom(mapping.getEffectiveFrom())
    // .effectiveTo(mapping.getEffectiveTo())
    // .build();
    // }

    // private String getPartialReasonName(Long reasonId) {
    // // Implement the logic to fetch the reason name based on the reasonId
    // return "Reason Name"; // Placeholder, replace with actual implementation
    // }

    // private List<SubClaimMapping> filterByPartialWithdrawalReason(
    // List<SubClaimMapping> mappings,
    // Long reasonId) {

    // if (mappings == null || mappings.isEmpty()) {
    // return List.of();
    // }

    // if (reasonId == null) {
    // return List.of();
    // }

    // return mappings.stream()
    // .filter(Objects::nonNull)
    // .filter(mapping -> mapping.getPartialReasonId() != null)
    // .filter(mapping -> Objects.equals(
    // mapping.getPartialReasonId(),
    // reasonId))
    // .toList();
    // }

    // private List<SubClaimMapping> filterByEffectiveDate(
    // List<SubClaimMapping> mappings,
    // LocalDate date) {

    // if (mappings == null || mappings.isEmpty()) {
    // return List.of();
    // }

    // return mappings.stream()
    // .filter(Objects::nonNull)
    // .filter(mapping -> isActive(
    // date,
    // mapping.getEffectiveFrom(),
    // mapping.getEffectiveTo()))
    // .toList();
    // }

    // private boolean isActive(
    // LocalDate date,
    // LocalDate effectiveFrom,
    // LocalDate effectiveTo) {

    // if (date == null) {
    // return false;
    // }

    // if (effectiveFrom != null && date.isBefore(effectiveFrom)) {
    // return false;
    // }

    // if (effectiveTo != null && date.isAfter(effectiveTo)) {
    // return false;
    // }

    // return true;
    // }

    // private List<SubClaimMapping> filterByMinContribution(
    // List<SubClaimMapping> mappings,
    // Integer totalContributionMonths) {

    // if (mappings == null || mappings.isEmpty()) {
    // return List.of();
    // }

    // if (totalContributionMonths == null) {
    // return List.of();
    // }

    // return mappings.stream()
    // .filter(Objects::nonNull)
    // .filter(mapping -> {
    // List<SubClaimCondition> conditions = subClaimConditionRepository
    // .findBySubClaimMapping_SubClaimCode(mapping.getSubClaimCode());

    // return conditions.stream().anyMatch(condition ->
    // matchesMinContribution(condition, totalContributionMonths));
    // })
    // .toList();
    // }

    // private boolean matchesMinContribution(
    // SubClaimCondition condition,
    // Integer totalContributionMonths) {

    // if (condition == null) {
    // return true;
    // }

    // if (totalContributionMonths == null) {
    // return false;
    // }

    // String expression = condition.getExpression();
    // Long duration = condition.getDuration();

    // if (expression == null || duration == null) {
    // return false;
    // }

    // return switch (expression.trim().toUpperCase()) {
    // case "GREATER_THAN" -> totalContributionMonths > duration;
    // case "GREATER_THAN_OR_EQUAL" -> totalContributionMonths >= duration;
    // case "LESS_THAN" -> totalContributionMonths < duration;
    // case "LESS_THAN_OR_EQUAL" -> totalContributionMonths <= duration;
    // case "EQUAL" -> totalContributionMonths.equals(duration.intValue());
    // default -> false;
    // };
    // }
}