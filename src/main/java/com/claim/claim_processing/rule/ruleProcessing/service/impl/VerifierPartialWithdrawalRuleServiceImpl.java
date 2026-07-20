package com.claim.claim_processing.rule.ruleProcessing.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;  // ← ADD THIS IMPORT
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.member.service.MemberService;
import com.claim.claim_processing.rule.claim.DTO.response.VerifierClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.VerifierClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleProcessing.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;
import com.claim.claim_processing.rule.ruleProcessing.service.VerifierPartialWithdrawalRuleService;

import lombok.*;

@Service
@RequiredArgsConstructor
public class VerifierPartialWithdrawalRuleServiceImpl implements VerifierPartialWithdrawalRuleService {
    private final RuleService ruleService;
    private final MemberService memberService;
    private final MemberContributionService memberContributionService;

    @Override
    public ApiResponseDTO<VerifierClaimCalculationResponseDTO> calculatePartialWithdrawal(
            ClaimInitialPreviewRequest request) {

        MemberDetailResponseDto memberDetail = getMemberDetail(request.getNppfNumber());

        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(memberDetail, null);

        ApiResponseDTO<List<MatchedSubClaimRuleDto>> ruleResponse = ruleService.playWithRule(request);
        System.out.println("rule size: "
                + (ruleResponse != null && ruleResponse.getData() != null ? ruleResponse.getData().size() : 0));
        List<MatchedSubClaimRuleDto> matchedRules = ruleResponse == null || ruleResponse.getData() == null
                ? List.of()
                : ruleResponse.getData();

        if (matchedRules.isEmpty()) {
            return ApiResponseDTO.notFound(
                    "No partial withdrawal rule found OR Your minimum contribution is less than the required threshold for partial withdrawal.");
        }

        List<ComponentBalanceDTO> components = new ArrayList<>();
        List<VerifierClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations = new ArrayList<>();

        BigDecimal finalPayableAmount = BigDecimal.ZERO;

        for (MatchedSubClaimRuleDto matchedRule : matchedRules) {

            BigDecimal withdrawalPercentage = matchedRule.getWithdrawalPercentage();

            if (withdrawalPercentage == null
                    || withdrawalPercentage.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // ========== PASS THE matchedRule TO GET subRuleCode ==========
            List<ComponentBalanceDTO> resolvedComponents = getRuleAmountUsingFormulaIfAvailable(
                    matchedRule,
                    request,
                    contributionSummary,
                    "PARTIAL_WITHDRAWAL",
                    expressionCalculations);

            if (resolvedComponents == null || resolvedComponents.isEmpty()) {
                continue;
            }

            for (ComponentBalanceDTO component : resolvedComponents) {

                BigDecimal baseAmount = component.getAmount() == null
                        ? BigDecimal.ZERO
                        : component.getAmount();

                BigDecimal partialAmount = baseAmount
                        .multiply(withdrawalPercentage)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                if (partialAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                // ========== COMPONENT ALREADY HAS subRuleCode FROM THE METHOD ==========
                // Just add it to the list
                components.add(component);

                finalPayableAmount = finalPayableAmount.add(partialAmount);
            }
        }

        VerifierClaimCalculationResponseDTO response = VerifierClaimCalculationResponseDTO.builder()
                .nppfNumber(contributionSummary.getNppfNumber())
                .contributionStartDate(memberDetail.getPfJoiningDate())
                .contributionEndDate(contributionSummary.getContributionEndDate())
                .totalContributionMonths(contributionSummary.getTotalContributionMonths())
                .totalNonContributionMonths(contributionSummary.getTotalNonContributionMonths())
                .components(components)
                .totalAmount(contributionSummary.getTotalBalance())
                .finalPayableAmount(finalPayableAmount)
                .expressionCalculations(expressionCalculations)
                .forfeitedComponents(Collections.emptyList())
                .eligibilityNote("Partial withdrawal calculated using component expression and withdrawal percentage.")
                .build();

        return ApiResponseDTO.success(response);
    }

    private List<ComponentBalanceDTO> getRuleAmountUsingFormulaIfAvailable(
            MatchedSubClaimRuleDto matchedRule,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary,
            String calculationType,
            List<VerifierClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

        if (matchedRule == null
                || matchedRule.getComponentMapping() == null
                || matchedRule.getComponentMapping().getExpressions() == null
                || matchedRule.getComponentMapping().getExpressions().isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, BigDecimal> componentAmountMap = buildContributionComponentMap(contributionSummary);

        List<ComponentBalanceDTO> results = new ArrayList<>();

        BigDecimal withdrawalPercentage = matchedRule.getWithdrawalPercentage();
        if (withdrawalPercentage == null) {
            withdrawalPercentage = BigDecimal.ZERO;
        }

        // ========== GET THE SUB_CLAIM_CODE ==========
        String subRuleCode = matchedRule.getSubClaimCode();

        for (MatchedSubClaimRuleDto.ComponentExpression expressionDto : matchedRule.getComponentMapping()
                .getExpressions()) {

            if (expressionDto == null
                    || expressionDto.getExpression() == null
                    || expressionDto.getExpression().isBlank()) {
                continue;
            }

            String expression = expressionDto.getExpression();

            List<String> resolvedCodes = resolveExpressionComponentCodes(
                    expression,
                    matchedRule.getComponentMapping(),
                    componentAmountMap);

            BigDecimal expressionAmount = resolvedCodes.stream()
                    .map(code -> componentAmountMap.getOrDefault(code, BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal percentalWithdrawalAmount = expressionAmount
                    .multiply(withdrawalPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // ========== ADD TO EXPRESSION CALCULATIONS ==========
            if (expressionCalculations != null) {
                expressionCalculations.add(
                        VerifierClaimCalculationResponseDTO.ExpressionCalculationDTO.builder()
                                .expression(expression)
                                .resolvedCodes(resolvedCodes)
                                .expressionAmount(expressionAmount)
                                .withdrawalPercentage(withdrawalPercentage)
                                .type(calculationType)
                                .precentalWithDrawalAmount(percentalWithdrawalAmount)
                                .build());
            }

            for (String componentCode : resolvedCodes) {

                BigDecimal amount = componentAmountMap.getOrDefault(
                        componentCode,
                        BigDecimal.ZERO);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                BigDecimal componentPercentalAmount = amount
                        .multiply(withdrawalPercentage)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                results.add(
                        ComponentBalanceDTO.builder()
                                .subRuleCode(subRuleCode)  // ========== ADD SUB_RULE_CODE ==========
                                .code(componentCode)
                                .name(componentCode)
                                .type(calculationType)
                                .amount(amount)
                                .percentalAmount(componentPercentalAmount)
                                .build());
            }
        }

        return results;
    }

    private List<String> resolveExpressionComponentCodes(
            String expression,
            MatchedSubClaimRuleDto.ComponentMapping mapping,
            Map<String, BigDecimal> componentAmountMap) {

        if (expression == null || expression.isBlank()
                || componentAmountMap == null) {
            return Collections.emptyList();
        }

        String[] tokens = expression
                .replace(" ", "")
                .toUpperCase()
                .split("[+\\-]");

        return Arrays.stream(tokens)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .filter(componentAmountMap::containsKey)
                .distinct()
                .toList();
    }

    private Map<String, BigDecimal> buildContributionComponentMap(
            MemberContributionSummary contributionSummary) {

        Map<String, BigDecimal> map = new HashMap<>();

        Map<String, String> dbColumnMapping = new HashMap<>();
        dbColumnMapping.put("PF_EC", "PF_EC");
        dbColumnMapping.put("PF_MC", "PF_MC");
        dbColumnMapping.put("P_EC", "PENSION_EC");
        dbColumnMapping.put("GC", "GC");
        dbColumnMapping.put("VC", "VC");
        dbColumnMapping.put("PF_IEC", "INTEREST_EC");
        dbColumnMapping.put("PF_IMC", "INTEREST_MC");
        dbColumnMapping.put("P_IEC", "INTEREST_PENSION");
        dbColumnMapping.put("IGC", "INTEREST_GC");
        dbColumnMapping.put("IVC", "INTEREST_VC");

        if (contributionSummary == null
                || contributionSummary.getComponentGroups() == null) {
            return map;
        }

        for (MemberContributionSummary.ComponentGroup component : contributionSummary.getComponentGroups()) {

            if (component == null || component.getComponentCode() == null) {
                continue;
            }

            String systemCode = component.getComponentCode().trim().toUpperCase();

            if (component.getPrincipalAmount() != null &&
                    component.getPrincipalAmount().compareTo(BigDecimal.ZERO) != 0) {

                BigDecimal principalAmount = component.getPrincipalAmount();
                String dbColumn = dbColumnMapping.getOrDefault(systemCode, systemCode);
                map.put(dbColumn, principalAmount);
                map.put(systemCode, principalAmount);
            }

            if (component.getInterestAmount() != null &&
                    component.getInterestAmount().compareTo(BigDecimal.ZERO) != 0) {

                BigDecimal interestAmount = component.getInterestAmount();
                String interestKey = systemCode;
                String dbColumn = dbColumnMapping.getOrDefault(interestKey, interestKey);
                map.put(dbColumn, interestAmount);
                map.put(interestKey, interestAmount);
            }
        }

        return map;
    }

    private MemberDetailResponseDto getMemberDetail(String nppfNumber) {

        ApiResponseDTO<MemberDetailResponseDto> response = memberService.getMemberDetails(nppfNumber);

        if (response == null || response.getData() == null) {
            throw new RuntimeException(
                    "Member detail not found for nppfNumber: " + nppfNumber);
        }

        return response.getData();
    }
}
