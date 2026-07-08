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
    private final RefundTypeRepository refundTypeRepository;

    @Override
    public ApiResponseDTO<List<MatchedSubClaimRuleDto>> playWithRule(
            ClaimInitialPreviewRequest request) {

        try {
            System.out.println("========== START RULE EVALUATION ==========");
            System.out.println("Request: " + request);
            
            validateRequest(request);

            MemberDetailResponseDto memberDetail = getMemberDetail(request.getNppfNumber());
            MemberContributionSummary contributionSummary = getContributionSummary(request.getNppfNumber(), request.getIdentityNumber());

            System.out.println("========== MEMBER DATA ==========");
            System.out.println("NPPF Number: " + request.getNppfNumber());
            System.out.println("Member Category ID: " + memberDetail.getMemberCategoryId());
            System.out.println("Scheme Type ID: " + contributionSummary.getSchemeTypeId());

            String memberCategoryId = memberDetail.getMemberCategoryId();
            Long schemeTypeId = contributionSummary.getSchemeTypeId();

            LocalDate cessationDate = resolveEndDate(request, contributionSummary);
            LocalDate serviceStartDate = toLocalDate(memberDetail.getDateOfServiceJoiningDate());

            Integer totalServiceMonths = calculateMonths(serviceStartDate, cessationDate);
            Integer totalServiceYears = totalServiceMonths == null ? null : totalServiceMonths / 12;

            Integer totalContributionMonths = contributionSummary.getTotalContributionMonths();
            Integer totalContributionYears = contributionSummary.getTotalContributionYears();
            Integer totalNonContributionMonths = contributionSummary.getTotalNonContributionMonths();

            System.out.println("========== CALCULATED VALUES ==========");
            System.out.println("Cessation Date: " + cessationDate);
            System.out.println("Total Service Months: " + totalServiceMonths);
            System.out.println("Total Service Years: " + totalServiceYears);
            System.out.println("Total Contribution Months: " + totalContributionMonths);
            System.out.println("Total Contribution Years: " + totalContributionYears);
            System.out.println("Total Non-Contribution Months: " + totalNonContributionMonths);

            boolean partialClaim = isPartialWithdrawalClaim(request.getClaimTypeId());
            boolean terminationClaim = isTerminationClaim(request.getCessationTypeId());

            System.out.println("========== CLAIM TYPE INFO ==========");
            System.out.println("Claim Type ID: " + request.getClaimTypeId());
            System.out.println("Is Partial Claim: " + partialClaim);
            System.out.println("Cessation Type ID: " + request.getCessationTypeId());
            System.out.println("Is Termination Claim: " + terminationClaim);

            List<ClaimTypeRuleMap> claimTypeRuleMaps = getClaimTypeRuleMaps(request.getClaimTypeId());
            
            System.out.println("========== CLAIM TYPE RULE MAPS ==========");
            System.out.println("Total Rule Maps Found: " + claimTypeRuleMaps.size());
            claimTypeRuleMaps.forEach(ctrm -> {
                System.out.println("  - Rule ID: " + ctrm.getRuleType().getId() + 
                        ", Rule Code: " + ctrm.getRuleType().getCode() + 
                        ", Rule Name: " + ctrm.getRuleType().getName());
            });

            List<String> ruleTypeCodes = claimTypeRuleMaps.stream()
                    .filter(Objects::nonNull)
                    .map(ClaimTypeRuleMap::getRuleType)
                    .filter(Objects::nonNull)
                    .map(RuleTypeMaster::getCode)
                    .filter(Objects::nonNull)
                    .toList();

            System.out.println("========== RULE TYPE CODES ==========");
            ruleTypeCodes.forEach(code -> System.out.println("  - " + code));

            List<SubClaimMapping> subClaimMappings = getSubClaimMappings(ruleTypeCodes);

            System.out.println("========== ALL SUB CLAIM MAPPINGS ==========");
            System.out.println("Total Mappings: " + subClaimMappings.size());
            subClaimMappings.forEach(m -> {
                System.out.println("  - " + m.getSubClaimCode() + " | Rule: " + 
                        (m.getRuleType() != null ? m.getRuleType().getCode() : "null") + 
                        " | Category: " + 
                        (m.getCategorySchemeMapping() != null ? m.getCategorySchemeMapping().getCategorySchemeCode() : "null") +
                        " | Time: " + 
                        (m.getTimeIndication() != null ? m.getTimeIndication().getTimeIndication() : "null"));
            });

            CategorySchemeMapping normalCategorySchemeMapping = getCategorySchemeMapping(schemeTypeId,
                    memberCategoryId);

            CategorySchemeMapping vestingCategorySchemeMapping = getVestingCategorySchemeMapping(memberCategoryId);

            System.out.println("========== CATEGORY SCHEME MAPPINGS ==========");
            System.out.println("Normal Category Scheme: " + 
                    (normalCategorySchemeMapping != null ? normalCategorySchemeMapping.getCategorySchemeCode() : "null"));
            System.out.println("Vesting Category Scheme: " + 
                    (vestingCategorySchemeMapping != null ? vestingCategorySchemeMapping.getCategorySchemeCode() : "null"));

            // =============================================
            // STEP 1: REASON/TERMINATION FILTER
            // =============================================
            System.out.println("\n========== STEP 1: REASON/TERMINATION FILTER ==========");
            System.out.println("Filtering for: " + (partialClaim ? "PARTIAL" : (terminationClaim ? "TERMINATION" : "NORMAL")));

            List<SubClaimMapping> reasonOrTerminationFiltered;

            if (partialClaim) {
                reasonOrTerminationFiltered = subClaimMappings.stream()
                        .filter(Objects::nonNull)
                        .filter(mapping -> {
                            boolean matches = Objects.equals(
                                    mapping.getPartialReasonId(),
                                    request.getReasonTypeId());
                            System.out.println("  Partial check: " + mapping.getSubClaimCode() + 
                                    " | PartialReasonId: " + mapping.getPartialReasonId() + 
                                    " | RequestReasonId: " + request.getReasonTypeId() +
                                    " | Matches: " + matches);
                            return matches;
                        })
                        .toList();
            } else {
                reasonOrTerminationFiltered = subClaimMappings.stream()
                        .filter(Objects::nonNull)
                        .filter(mapping -> {
                            boolean isVesting = isVestingRule(mapping);
                            boolean isTermRule = isTerminationRule(mapping);
                            boolean pass;

                            if (isVesting) {
                                pass = true;
                            } else if (terminationClaim) {
                                pass = isTermRule;
                            } else {
                                pass = !isTermRule;
                            }

                            System.out.println("  Filter: " + mapping.getSubClaimCode() + 
                                    " | RuleType: " + (mapping.getRuleType() != null ? mapping.getRuleType().getCode() : "null") +
                                    " | isVesting: " + isVesting +
                                    " | isTerminationRule: " + isTermRule +
                                    " | Pass: " + pass);
                            return pass;
                        })
                        .toList();
            }

            System.out.println("\nAfter Reason/Termination Filter: " + reasonOrTerminationFiltered.size() + " rules");
            reasonOrTerminationFiltered.forEach(m -> 
                System.out.println("  - " + m.getSubClaimCode() + " | " + 
                    (m.getRuleType() != null ? m.getRuleType().getCode() : "null")));

            // =============================================
            // STEP 2: CATEGORY FILTER
            // =============================================
            System.out.println("\n========== STEP 2: CATEGORY FILTER ==========");

            List<SubClaimMapping> categoryFiltered = reasonOrTerminationFiltered.stream()
                    .filter(mapping -> {
                        boolean isVesting = isVestingRule(mapping);
                        boolean matches;

                        if (isVesting) {
                            matches = matchesCategoryScheme(mapping, vestingCategorySchemeMapping);
                        } else {
                            matches = matchesCategoryScheme(mapping, normalCategorySchemeMapping);
                        }

                        System.out.println("  Category check: " + mapping.getSubClaimCode() + 
                                " | isVesting: " + isVesting +
                                " | CategoryScheme: " + 
                                (mapping.getCategorySchemeMapping() != null ? mapping.getCategorySchemeMapping().getCategorySchemeCode() : "null") +
                                " | Target: " + (isVesting ? 
                                    (vestingCategorySchemeMapping != null ? vestingCategorySchemeMapping.getCategorySchemeCode() : "null") :
                                    (normalCategorySchemeMapping != null ? normalCategorySchemeMapping.getCategorySchemeCode() : "null")) +
                                " | Matches: " + matches);
                        return matches;
                    })
                    .toList();

            System.out.println("\nAfter Category Filter: " + categoryFiltered.size() + " rules");
            categoryFiltered.forEach(m -> 
                System.out.println("  - " + m.getSubClaimCode()));

            // =============================================
            // STEP 3: TIME FILTER
            // =============================================
            System.out.println("\n========== STEP 3: TIME FILTER ==========");

            List<SubClaimMapping> timeFiltered = categoryFiltered.stream()
                    .filter(mapping -> {
                        boolean matches = matchesTimeIndication(
                                mapping.getTimeIndication(),
                                cessationDate);
                        
                        System.out.println("  Time check: " + mapping.getSubClaimCode() + 
                                " | TimeIndication: " + 
                                (mapping.getTimeIndication() != null ? mapping.getTimeIndication().getTimeIndication() : "null") +
                                " | CessationDate: " + cessationDate +
                                " | Matches: " + matches);
                        return matches;
                    })
                    .toList();

            System.out.println("\nAfter Time Filter: " + timeFiltered.size() + " rules");
            timeFiltered.forEach(m -> 
                System.out.println("  - " + m.getSubClaimCode()));

            // =============================================
            // STEP 4: CONDITION FILTER
            // =============================================
            System.out.println("\n========== STEP 4: CONDITION FILTER ==========");

            List<SubClaimMapping> matchedMappings = timeFiltered.stream()
                    .filter(mapping -> {
                        boolean matches = partialClaim
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
                                        totalServiceYears);
                        
                        System.out.println("  Condition check: " + mapping.getSubClaimCode() + 
                                " | Matches: " + matches);
                        return matches;
                    })
                    .toList();

            System.out.println("\nAfter Condition Filter: " + matchedMappings.size() + " rules");
            matchedMappings.forEach(m -> 
                System.out.println("  - " + m.getSubClaimCode()));

            // =============================================
            // FINAL RESULT
            // =============================================
            System.out.println("\n========== FINAL RESULT ==========");
            
            if (matchedMappings.isEmpty()) {
                System.out.println("❌ NO MATCHING RULES FOUND");
                System.out.println("Summary of filters:");
                System.out.println("  - Total SubClaimMappings: " + subClaimMappings.size());
                System.out.println("  - After Reason/Termination: " + reasonOrTerminationFiltered.size());
                System.out.println("  - After Category: " + categoryFiltered.size());
                System.out.println("  - After Time: " + timeFiltered.size());
                System.out.println("  - After Conditions: " + matchedMappings.size());
                
                // Print details of why each rule failed
                System.out.println("\n========== DETAILED FAILURE ANALYSIS ==========");
                timeFiltered.forEach(mapping -> {
                    System.out.println("\n--- Rule: " + mapping.getSubClaimCode() + " ---");
                    List<SubClaimCondition> conditions = subClaimConditionRepository
                            .findBySubClaimMapping_SubClaimCode(mapping.getSubClaimCode());
                    if (conditions == null || conditions.isEmpty()) {
                        System.out.println("  ❌ No conditions found (should have passed)");
                    } else {
                        System.out.println("  Conditions count: " + conditions.size());
                        conditions.forEach(cond -> {
                            boolean conditionMet = matchesCondition(
                                    cond,
                                    cessationDate,
                                    totalContributionMonths,
                                    totalContributionYears,
                                    totalNonContributionMonths,
                                    totalServiceMonths,
                                    totalServiceYears);
                            System.out.println("    - " + cond.getConditionCode() + 
                                    " | Check: " + cond.getConditionCheck() +
                                    " | Expression: " + cond.getExpression() +
                                    " | Duration: " + cond.getDuration() +
                                    " | Actual Value: " + getActualValue(
                                        cond.getConditionCheck(),
                                        totalContributionMonths,
                                        totalContributionYears,
                                        totalNonContributionMonths,
                                        totalServiceMonths,
                                        totalServiceYears) +
                                    " | Passed: " + conditionMet);
                        });
                    }
                });
                
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

            System.out.println("✅ Matching rules found: " + response.size());
            response.forEach(r -> System.out.println("  - " + r.getSubClaimCode()));
            System.out.println("========== END RULE EVALUATION ==========\n");

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
        
        String ruleType = mapping.getRuleType() == null
                ? ""
                : mapping.getRuleType().getCode().trim().toUpperCase();
        
        String subClaimType = mapping.getSubClaimType() == null
                ? ""
                : mapping.getSubClaimType().trim().toUpperCase();
        
        String subClaimCode = mapping.getSubClaimCode() == null
                ? ""
                : mapping.getSubClaimCode().trim().toUpperCase();
        
        boolean isVesting = ruleType.contains("VEST") 
                || subClaimType.contains("VEST")
                || subClaimCode.contains("VEST");
        
        if (isVesting) {
            System.out.println("  ✅ isVestingRule: " + mapping.getSubClaimCode() + 
                    " | ruleType=" + ruleType + 
                    " | subClaimType=" + subClaimType);
        }
        
        return isVesting;
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
            return true;
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
        
        // If no conditions, rule is always applicable
        if (conditions == null || conditions.isEmpty()) {
            System.out.println("  ✅ No conditions for " + mapping.getSubClaimCode() + " - ALWAYS applicable");
            return true;
        }
        
        System.out.println("  🔍 Checking " + conditions.size() + " conditions for " + mapping.getSubClaimCode());
        
        // ALL conditions must be met (AND logic)
        return conditions.stream()
                .allMatch(condition -> matchesCondition(
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
            System.out.println("  ❌ Condition is null");
            return false;
        }

        // Check effective date first
        if (!matchesEffectiveDate(condition, endDate)) {
            System.out.println("  ❌ Failed effective date check for: " + condition.getConditionCode());
            return false;
        }

        String conditionCheck = condition.getConditionCheck();
        String expression = condition.getExpression();
        Long duration = condition.getDuration();

        if (conditionCheck == null || expression == null || duration == null) {
            System.out.println("  ❌ Required fields missing for: " + condition.getConditionCode());
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
            System.out.println("  ❌ Actual value is null for check: " + conditionCheck);
            return false;
        }

        boolean result = evaluateExpression(actualValue, duration, expression);
        
        System.out.println("  📊 " + condition.getConditionCode() + 
                " | Check: " + conditionCheck +
                " | Expression: " + expression +
                " | Duration: " + duration +
                " | Actual: " + actualValue +
                " | Result: " + (result ? "✅ PASS" : "❌ FAIL"));
        
        return result;
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

    private boolean matchesEffectiveDate(SubClaimCondition condition, LocalDate endDate) {
        if (condition == null) {
            return true;
        }

        LocalDate effectiveFrom = condition.getEffectiveFrom();
        LocalDate effectiveTo = condition.getEffectiveTo();

        if (effectiveFrom == null && effectiveTo == null) {
            return true;
        }

        if (effectiveFrom != null && endDate.isBefore(effectiveFrom)) {
            return false;
        }

        if (effectiveTo != null && endDate.isAfter(effectiveTo)) {
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

    private MemberContributionSummary getContributionSummary(String nppfNumber, String identityNumber) {

        MemberContributionSummary summary = memberContributionService.getContributionSummary(nppfNumber, identityNumber);

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
            return ((java.sql.Date) date).toLocalDate();
        }

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
            System.out.println("⚠️ CessationTypeId is null or <= 0: " + cessationTypeId);
            return false;
        }
        
        try {
            CessationTypeMaster cessationType = cessationTypeRepository.findById(cessationTypeId)
                    .orElseThrow(() -> ClaimException.notFound(null));

            String code = cessationType.getCode();
            String name = cessationType.getName();

            System.out.println("========== TERMINATION CHECK DEBUG ==========");
            System.out.println("Cessation Type ID: " + cessationTypeId);
            System.out.println("Cessation Type Code: " + code);
            System.out.println("Cessation Type Name: " + name);
            System.out.println("=============================================");

            // Check if it's a termination type
            if (code == null) {
                return false;
            }
            
            String upperCode = code.toUpperCase();
            boolean isTermination = upperCode.equals("TERMINATION") 
                    || upperCode.equals("TERM")
                    || upperCode.equals("RESIGNATION")
                    || upperCode.equals("RESIGN")
                    || upperCode.equals("DISMISSAL")
                    || upperCode.equals("DISMISS")
                    || upperCode.equals("SEPARATION")
                    || upperCode.equals("QUIT");
            
            System.out.println("Is Termination: " + isTermination);
            return isTermination;
            
        } catch (ClaimException e) {
            System.out.println("❌ Error finding cessation type: " + e.getMessage());
            return false;
        }
    }

    private boolean isTerminationRule(SubClaimMapping mapping) {

        if (mapping == null || mapping.getRuleType() == null) {
            return false;
        }

        String code = mapping.getRuleType().getCode();
        if (code == null) {
            return false;
        }

        String upperCode = code.toUpperCase();
        
        // Check if it's a termination-related rule
        return upperCode.contains("TERM")
                || upperCode.contains("TERMINATION")
                || upperCode.contains("LAPSED");
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
                .map(refundType -> refundType.getName())
                .orElse(null);
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

        if (mapping == null) {
            System.out.println("  ❌ Mapping is null");
            return false;
        }

        // If mapping has no category scheme, it applies to ALL categories
        if (mapping.getCategorySchemeMapping() == null) {
            System.out.println("  ✅ No category scheme on mapping - matches ALL");
            return true;
        }

        if (categorySchemeMapping == null) {
            System.out.println("  ❌ Target category scheme is null");
            return false;
        }

        String mappingCategory = mapping.getCategorySchemeMapping().getCategorySchemeCode();
        String targetCategory = categorySchemeMapping.getCategorySchemeCode();

        boolean matches = Objects.equals(mappingCategory, targetCategory);
        System.out.println("  Category match: " + matches + 
                " (" + mappingCategory + " vs " + targetCategory + ")");
        
        return matches;
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
                .categoryName(categorySchemeMapping.getAgencyCategory() != null
                        ? categorySchemeMapping.getAgencyCategory().getCategoryName()
                        : null)
                .schemeTypeId(
                        categorySchemeMapping.getSchemeType() != null ? categorySchemeMapping.getSchemeType().getId()
                                : null)
                .schemeTypeName(
                        categorySchemeMapping.getSchemeType() != null ? categorySchemeMapping.getSchemeType().getName()
                                : null)
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