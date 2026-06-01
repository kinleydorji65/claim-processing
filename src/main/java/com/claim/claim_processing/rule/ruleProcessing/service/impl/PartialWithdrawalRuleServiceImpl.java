package com.claim.claim_processing.rule.ruleProcessing.service.impl;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.member.service.MemberService;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleGateWay.dto.MatchedSubClaimRuleDto;
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
            return ApiResponseDTO.notFound("No partial withdrawal rule found");
        }

        List<ComponentBalanceDTO> components = new ArrayList<>();
        List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations = new ArrayList<>();

        BigDecimal finalPayableAmount = BigDecimal.ZERO;

        for (MatchedSubClaimRuleDto matchedRule : matchedRules) {

            BigDecimal calculatedAmount = calculatePartialWithdrawalAmount(
                    matchedRule,
                    contributionSummary,
                    expressionCalculations);

            if (calculatedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            finalPayableAmount = finalPayableAmount.add(calculatedAmount);

            components.add(
                    ComponentBalanceDTO.builder()
                            .code(
                                    matchedRule.getComponentMapping() != null
                                            ? matchedRule.getComponentMapping().getComponentMappingCode()
                                            : null)
                            .name("Partial Withdrawal Component")
                            .type("PARTIAL_WITHDRAWAL")
                            .amount(calculatedAmount)
                            .build());
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

    private BigDecimal calculatePartialWithdrawalAmount(
        MatchedSubClaimRuleDto matchedRule,
        MemberContributionSummary contributionSummary,
        List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations
) {

    if (matchedRule == null || matchedRule.getComponentMapping() == null) {
        return BigDecimal.ZERO;
    }

    BigDecimal withdrawalPercentage = matchedRule.getWithdrawalPercentage();

    if (withdrawalPercentage == null) {
        withdrawalPercentage = getWithdrawalPercentageFromCondition(matchedRule);
    }

    if (withdrawalPercentage == null || withdrawalPercentage.compareTo(BigDecimal.ZERO) <= 0) {
        return BigDecimal.ZERO;
    }

    Map<String, BigDecimal> componentMap = buildContributionComponentMap(contributionSummary);

    List<MatchedSubClaimRuleDto.ComponentExpression> expressions =
            matchedRule.getComponentMapping().getExpressions();

    if (expressions == null || expressions.isEmpty()) {
        return BigDecimal.ZERO;
    }

    BigDecimal totalExpressionAmount = BigDecimal.ZERO;

    for (MatchedSubClaimRuleDto.ComponentExpression expressionDto : expressions) {

        if (expressionDto == null
                || expressionDto.getExpression() == null
                || expressionDto.getExpression().isBlank()) {
            continue;
        }

        String expression = expressionDto.getExpression();

        BigDecimal expressionAmount = evaluateExpression(
                expression,
                componentMap
        );

        totalExpressionAmount = totalExpressionAmount.add(expressionAmount);

        expressionCalculations.add(
                ClaimCalculationResponseDTO.ExpressionCalculationDTO.builder()
                        .expression(expression)
                        .resolvedCodes(resolveExpressionComponentCodes(
                                expression,
                                matchedRule.getComponentMapping()
                        ))
                        .expressionAmount(expressionAmount)
                        .withdrawalPercentage(withdrawalPercentage)
                        .type("PARTIAL_WITHDRAWAL")
                        .build()
        );
    }

    return totalExpressionAmount
            .multiply(withdrawalPercentage)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
}

private BigDecimal getWithdrawalPercentageFromCondition(
        MatchedSubClaimRuleDto matchedRule
) {

    if (matchedRule == null || matchedRule.getCondition() == null) {
        return BigDecimal.ZERO;
    }

    Long duration = matchedRule.getCondition().getDuration();

    return duration == null
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(duration);
}

    private List<String> resolveExpressionComponentCodes(
            String expression,
            MatchedSubClaimRuleDto.ComponentMapping mapping) {

        if (expression == null || expression.isBlank() || mapping == null) {
            return Collections.emptyList();
        }

        boolean hasPf = "Y".equalsIgnoreCase(mapping.getHasPf());
        boolean hasPc = "Y".equalsIgnoreCase(mapping.getHasPc());

        String[] tokens = expression
                .replace(" ", "")
                .toUpperCase()
                .split("[+\\-]");

        List<String> resolvedCodes = new ArrayList<>();

        for (String token : tokens) {

            if (token == null || token.isBlank()) {
                continue;
            }

            if (hasPf) {
                resolvedCodes.add("PF_" + token);
            }

            if (hasPc) {
                resolvedCodes.add("PC_" + token);
            }
        }

        return resolvedCodes.stream()
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

    private BigDecimal evaluateExpression(
            String expression,
            Map<String, BigDecimal> componentAmountMap) {

        if (expression == null || expression.isBlank()) {
            return BigDecimal.ZERO;
        }

        if (componentAmountMap == null || componentAmountMap.isEmpty()) {
            return BigDecimal.ZERO;
        }

        String cleanExpression = expression
                .replace(" ", "")
                .toUpperCase();

        List<String> tokens = new ArrayList<>();
        List<Character> operators = new ArrayList<>();

        StringBuilder currentToken = new StringBuilder();

        for (char ch : cleanExpression.toCharArray()) {

            if (ch == '+' || ch == '-') {
                tokens.add(currentToken.toString());
                operators.add(ch);
                currentToken.setLength(0);
                continue;
            }

            currentToken.append(ch);
        }

        tokens.add(currentToken.toString());

        BigDecimal result = getTokenValue(
                tokens.get(0),
                componentAmountMap);

        for (int i = 1; i < tokens.size(); i++) {

            BigDecimal value = getTokenValue(
                    tokens.get(i),
                    componentAmountMap);

            char operator = operators.get(i - 1);

            switch (operator) {
                case '+':
                    result = result.add(value);
                    break;

                case '-':
                    result = result.subtract(value);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        }

        return result;
    }

    private BigDecimal getTokenValue(
            String token,
            Map<String, BigDecimal> componentAmountMap) {

        if (token == null || token.isBlank()) {
            return BigDecimal.ZERO;
        }

        String code = token.trim().toUpperCase();

        if (componentAmountMap.containsKey(code)) {
            return componentAmountMap.get(code);
        }

        String pfCode = "PF_" + code;

        if (componentAmountMap.containsKey(pfCode)) {
            return componentAmountMap.get(pfCode);
        }

        String pcCode = "PC_" + code;

        return componentAmountMap.getOrDefault(pcCode, BigDecimal.ZERO);
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