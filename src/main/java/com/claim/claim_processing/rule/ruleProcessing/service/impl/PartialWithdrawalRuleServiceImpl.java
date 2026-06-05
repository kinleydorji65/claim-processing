package com.claim.claim_processing.rule.ruleProcessing.service.impl;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.member.service.MemberService;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleProcessing.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleProcessing.service.PartialWithdrawalRuleService;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PartialWithdrawalRuleServiceImpl implements PartialWithdrawalRuleService {

    private final RuleService ruleService;
    private final MemberService memberService;
    private final MemberContributionService memberContributionService;

    @Override
    public ApiResponseDTO<ClaimCalculationResponseDTO> calculatePartialWithdrawal(
            ClaimInitialPreviewRequest request) {

        MemberDetailResponseDto memberDetail = getMemberDetail(request.getNppfNumber());

        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(request.getNppfNumber());

        memberDetail.getDateOfServiceJoiningDate();

        ApiResponseDTO<List<MatchedSubClaimRuleDto>> ruleResponse = ruleService.playWithRule(request);
        System.out.println("rule size: "
                + (ruleResponse != null && ruleResponse.getData() != null ? ruleResponse.getData().size() : 0));
        List<MatchedSubClaimRuleDto> matchedRules = ruleResponse == null || ruleResponse.getData() == null
                ? List.of()
                : ruleResponse.getData();

        if (matchedRules.isEmpty()) {
            return ApiResponseDTO.notFound("No partial withdrawal rule found OR Your minimum contribution is less than the required threshold for partial withdrawal.");
        }

        List<ComponentBalanceDTO> components = new ArrayList<>();
        List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations = new ArrayList<>();

        BigDecimal finalPayableAmount = BigDecimal.ZERO;

        for (MatchedSubClaimRuleDto matchedRule : matchedRules) {

            BigDecimal withdrawalPercentage = matchedRule.getWithdrawalPercentage();

            if (withdrawalPercentage == null
                    || withdrawalPercentage.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

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

                components.add(
                        ComponentBalanceDTO.builder()
                                .code(component.getCode())
                                .name(component.getName())
                                .type("PARTIAL_WITHDRAWAL")
                                .amount(component.getAmount())
                                .build());

                finalPayableAmount = finalPayableAmount.add(partialAmount);
            }
        }

        ClaimCalculationResponseDTO response = ClaimCalculationResponseDTO.builder()
                .nppfNumber(contributionSummary.getNppfNumber())
                .contributionStartDate(contributionSummary.getContributionStartDate())
                .contributionEndDate(contributionSummary.getContributionEndDate())
                .totalContributionMonths(contributionSummary.getTotalContributionMonths())
                .totalNonContributionMonths(contributionSummary.getTotalNonContributionMonths())
                .components(components)
                .finalPayableAmount(finalPayableAmount)
                .expressionCalculations(expressionCalculations)
                .eligibilityNote("Partial withdrawal calculated using component expression and withdrawal percentage.")
                .build();

        return ApiResponseDTO.success(response);
    }

    private List<ComponentBalanceDTO> getRuleAmountUsingFormulaIfAvailable(
            MatchedSubClaimRuleDto matchedRule,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary,
            String calculationType,
            List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

        if (matchedRule == null
                || matchedRule.getComponentMapping() == null
                || matchedRule.getComponentMapping().getExpressions() == null
                || matchedRule.getComponentMapping().getExpressions().isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, BigDecimal> componentAmountMap = buildContributionComponentMap(contributionSummary);

        List<ComponentBalanceDTO> results = new ArrayList<>();

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

            if (expressionCalculations != null) {
                expressionCalculations.add(
                        ClaimCalculationResponseDTO.ExpressionCalculationDTO.builder()
                                .expression(expression)
                                .resolvedCodes(resolvedCodes)
                                .expressionAmount(expressionAmount)
                                .withdrawalPercentage(matchedRule.getWithdrawalPercentage())
                                .type(calculationType)
                                .build());
            }

            for (String componentCode : resolvedCodes) {

                BigDecimal amount = componentAmountMap.getOrDefault(
                        componentCode,
                        BigDecimal.ZERO);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                results.add(
                        ComponentBalanceDTO.builder()
                                .code(componentCode)
                                .name(componentCode)
                                .type(calculationType)
                                .amount(amount)
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

        if (contributionSummary == null
                || contributionSummary.getComponentGroups() == null) {
            return map;
        }

        for (MemberContributionSummary.ComponentGroup component : contributionSummary.getComponentGroups()) {

            if (component == null || component.getComponentCode() == null) {
                continue;
            }

            String fullCode = component.getComponentCode().trim().toUpperCase();

            String shortCode = fullCode.contains("_")
                    ? fullCode.substring(fullCode.lastIndexOf("_") + 1)
                    : fullCode;

            BigDecimal amount;

            if (shortCode.startsWith("I")) {
                amount = component.getInterestAmount() == null
                        ? BigDecimal.ZERO
                        : component.getInterestAmount();
            } else {
                amount = component.getPrincipalAmount() == null
                        ? BigDecimal.ZERO
                        : component.getPrincipalAmount();
            }

            map.put(fullCode, amount);
            map.put(shortCode, amount);
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