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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartialWithdrawalRuleServiceImpl implements PartialWithdrawalRuleService {

    private final RuleService ruleService;
    private final MemberService memberService;
    private final MemberContributionService memberContributionService;

    @Override
    public ApiResponseDTO<ClaimCalculationResponseDTO> calculatePartialWithdrawal(
            ClaimInitialPreviewRequest request) {

        log.info("=== START PARTIAL WITHDRAWAL CALCULATION ===");
        log.info("Request NPPF: {}", request.getNppfNumber());

        try {
            MemberDetailResponseDto memberDetail = getMemberDetail(request.getNppfNumber());
            log.info("Member Category: {}, Scheme: {}", 
                memberDetail.getMemberCategoryId(), 
                memberDetail.getSchemeTypeId());

            MemberContributionSummary contributionSummary = memberContributionService
                    .getContributionSummary(memberDetail, null);
            
            log.info("Contribution Summary - Total Balance: {}", contributionSummary.getTotalBalance());

            // Log component groups
            if (contributionSummary.getComponentGroups() != null) {
                for (MemberContributionSummary.ComponentGroup comp : contributionSummary.getComponentGroups()) {
                    log.info("Component: {} = {}", comp.getComponentCode(), comp.getTotalAmount());
                }
            }

            ApiResponseDTO<List<MatchedSubClaimRuleDto>> ruleResponse = ruleService.playWithRule(request);
            log.info("Rule response status: {}, data size: {}", 
                ruleResponse != null ? ruleResponse.getStatus() : "null",
                ruleResponse != null && ruleResponse.getData() != null ? ruleResponse.getData().size() : 0);
            
            List<MatchedSubClaimRuleDto> matchedRules = ruleResponse == null || ruleResponse.getData() == null
                    ? List.of()
                    : ruleResponse.getData();

            if (matchedRules.isEmpty()) {
                log.warn("No partial withdrawal rules found for request: {}", request);
                return ApiResponseDTO.notFound(
                        "No partial withdrawal rule found OR Your minimum contribution is less than the required threshold for partial withdrawal.");
            }

            List<ComponentBalanceDTO> components = new ArrayList<>();
            List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations = new ArrayList<>();

            BigDecimal finalPayableAmount = BigDecimal.ZERO;

            for (MatchedSubClaimRuleDto matchedRule : matchedRules) {

                BigDecimal withdrawalPercentage = matchedRule.getWithdrawalPercentage();
                log.info("Processing rule: {}, Withdrawal Percentage: {}%", 
                    matchedRule.getSubClaimCode(), withdrawalPercentage);

                if (withdrawalPercentage == null
                        || withdrawalPercentage.compareTo(BigDecimal.ZERO) <= 0) {
                    log.warn("Skipping rule {} - invalid percentage", matchedRule.getSubClaimCode());
                    continue;
                }

                // Build component map
                Map<String, BigDecimal> componentAmountMap = buildContributionComponentMap(contributionSummary);
                log.info("Component Amount Map Keys: {}", componentAmountMap.keySet());

                List<ComponentBalanceDTO> resolvedComponents = getRuleAmountUsingFormulaIfAvailable(
                        matchedRule,
                        componentAmountMap,
                        "PARTIAL_WITHDRAWAL",
                        expressionCalculations);

                if (resolvedComponents == null || resolvedComponents.isEmpty()) {
                    log.warn("No resolved components for rule: {}", matchedRule.getSubClaimCode());
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

                    components.add(component);
                    finalPayableAmount = finalPayableAmount.add(partialAmount);
                }
            }

            log.info("Final Payable Amount: {}", finalPayableAmount);

            ClaimCalculationResponseDTO response = ClaimCalculationResponseDTO.builder()
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

        } catch (Exception e) {
            log.error("Error calculating partial withdrawal: {}", e.getMessage(), e);
            // FIX: Use builder to create error response with correct type
            return ApiResponseDTO.<ClaimCalculationResponseDTO>builder()
                    .status(500L)
                    .message("Failed to calculate partial withdrawal: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    private List<ComponentBalanceDTO> getRuleAmountUsingFormulaIfAvailable(
            MatchedSubClaimRuleDto matchedRule,
            Map<String, BigDecimal> componentAmountMap,
            String calculationType,
            List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

        log.info("=== GET RULE AMOUNT USING FORMULA ===");
        log.info("Rule Code: {}", matchedRule.getSubClaimCode());

        if (matchedRule == null
                || matchedRule.getComponentMapping() == null
                || matchedRule.getComponentMapping().getExpressions() == null
                || matchedRule.getComponentMapping().getExpressions().isEmpty()) {
            log.warn("No component mapping or expressions found for rule: {}", 
                matchedRule != null ? matchedRule.getSubClaimCode() : "null");
            return Collections.emptyList();
        }

        List<ComponentBalanceDTO> results = new ArrayList<>();

        BigDecimal withdrawalPercentage = matchedRule.getWithdrawalPercentage();
        if (withdrawalPercentage == null) {
            withdrawalPercentage = BigDecimal.ZERO;
        }

        String subRuleCode = matchedRule.getSubClaimCode();

        for (MatchedSubClaimRuleDto.ComponentExpression expressionDto : matchedRule.getComponentMapping()
                .getExpressions()) {

            if (expressionDto == null
                    || expressionDto.getExpression() == null
                    || expressionDto.getExpression().isBlank()) {
                continue;
            }

            String expression = expressionDto.getExpression();
            log.info("Processing Expression: {}", expression);

            List<String> resolvedCodes = resolveExpressionComponentCodes(expression, componentAmountMap);
            log.info("Resolved Codes from expression: {}", resolvedCodes);

            if (resolvedCodes.isEmpty()) {
                log.warn("No codes resolved from expression: {}", expression);
                log.warn("Available codes: {}", componentAmountMap.keySet());
                continue;
            }

            BigDecimal expressionAmount = resolvedCodes.stream()
                    .map(code -> componentAmountMap.getOrDefault(code, BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal percentalWithdrawalAmount = expressionAmount
                    .multiply(withdrawalPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (expressionCalculations != null) {
                expressionCalculations.add(
                        ClaimCalculationResponseDTO.ExpressionCalculationDTO.builder()
                                .expression(expression)
                                .resolvedCodes(resolvedCodes)
                                .expressionAmount(expressionAmount)
                                .withdrawalPercentage(withdrawalPercentage)
                                .precentalWithDrawalAmount(percentalWithdrawalAmount)
                                .type(calculationType)
                                .build());
            }

            for (String componentCode : resolvedCodes) {

                BigDecimal amount = componentAmountMap.getOrDefault(
                        componentCode,
                        BigDecimal.ZERO);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    log.debug("Component {} has zero amount, skipping", componentCode);
                    continue;
                }

                BigDecimal componentPercentalAmount = amount
                        .multiply(withdrawalPercentage)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                results.add(
                        ComponentBalanceDTO.builder()
                                .subRuleCode(subRuleCode)
                                .code(componentCode)
                                .name(componentCode)
                                .type(calculationType)
                                .amount(amount)
                                .percentalAmount(componentPercentalAmount)
                                .build());
                
                log.info("Added component: {} = {}, Percental: {}", 
                    componentCode, amount, componentPercentalAmount);
            }
        }

        log.info("Returning {} results", results.size());
        return results;
    }

    private List<String> resolveExpressionComponentCodes(
            String expression,
            Map<String, BigDecimal> componentAmountMap) {

        if (expression == null || expression.isBlank()
                || componentAmountMap == null) {
            return Collections.emptyList();
        }

        log.info("=== RESOLVING EXPRESSION ===");
        log.info("Expression: {}", expression);
        log.info("Available codes: {}", componentAmountMap.keySet());

        String[] tokens = expression
                .replace(" ", "")
                .toUpperCase()
                .split("[+\\-]");

        List<String> resolved = Arrays.stream(tokens)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .filter(token -> {
                    boolean exists = componentAmountMap.containsKey(token);
                    if (!exists) {
                        log.warn("Component code '{}' NOT found in map", token);
                    } else {
                        log.info("Component code '{}' found with value: {}", token, componentAmountMap.get(token));
                    }
                    return exists;
                })
                .distinct()
                .toList();

        log.info("Resolved codes: {}", resolved);
        return resolved;
    }

    private Map<String, BigDecimal> buildContributionComponentMap(
            MemberContributionSummary contributionSummary) {

        Map<String, BigDecimal> map = new HashMap<>();

        if (contributionSummary == null
                || contributionSummary.getComponentGroups() == null) {
            log.warn("Contribution summary or component groups is null");
            return map;
        }

        log.info("Building component map from {} component groups", 
            contributionSummary.getComponentGroups().size());

        // Add all components from the contribution summary
        for (MemberContributionSummary.ComponentGroup component : contributionSummary.getComponentGroups()) {

            if (component == null || component.getComponentCode() == null) {
                continue;
            }

            String code = component.getComponentCode().trim().toUpperCase();
            BigDecimal totalAmount = component.getTotalAmount() != null ? component.getTotalAmount() : BigDecimal.ZERO;

            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                // Add with the exact code from the component group
                map.put(code, totalAmount);
                log.debug("Added component: {} = {}", code, totalAmount);
                
                // Also add with alternative keys if needed for expressions
                addAlternativeKeys(map, code, totalAmount);
            }
        }

        log.info("Built component map with {} entries: {}", map.size(), map.keySet());
        return map;
    }

    private void addAlternativeKeys(Map<String, BigDecimal> map, String code, BigDecimal amount) {
        // Add alternative keys for interest components
        if (code.equals("PF_IEC")) {
            map.put("INTEREST_EC", amount);
            map.put("PF_IEC_INTEREST", amount);
        } else if (code.equals("PF_IMC")) {
            map.put("INTEREST_MC", amount);
            map.put("PF_IMC_INTEREST", amount);
        } else if (code.equals("P_IEC")) {
            map.put("INTEREST_PENSION", amount);
            map.put("P_IEC_INTEREST", amount);
        } else if (code.equals("IGC")) {
            map.put("INTEREST_GC", amount);
            map.put("IGC_INTEREST", amount);
        } else if (code.equals("IVC")) {
            map.put("INTEREST_VC", amount);
            map.put("IVC_INTEREST", amount);
        }
        
        // Add alternative keys for principal components
        if (code.equals("PF_EC")) {
            map.put("EMPLOYEE_PF", amount);
        } else if (code.equals("PF_MC")) {
            map.put("EMPLOYER_PF", amount);
        } else if (code.equals("P_EC")) {
            map.put("PENSION", amount);
            map.put("PENSION_EC", amount);
        } else if (code.equals("GC")) {
            map.put("GOVERNMENT", amount);
        } else if (code.equals("VC")) {
            map.put("VOLUNTARY", amount);
        }
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