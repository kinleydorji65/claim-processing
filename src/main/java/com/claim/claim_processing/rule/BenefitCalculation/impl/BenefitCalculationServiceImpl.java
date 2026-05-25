package com.claim.claim_processing.rule.BenefitCalculation.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.formula.dto.ClaimFormulaResponseDto;
import com.claim.claim_processing.rule.formula.dto.FormulaComponentMapResponseDto;
import com.claim.claim_processing.rule.formula.service.FormulaService;
import com.claim.claim_processing.rule.ruleGateWay.dto.MatchedConditionRuleDto;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BenefitCalculationServiceImpl implements BenefitCalculationService {

    private final MemberContributionService memberContributionService;
    private final RuleService ruleService;
    private final FormulaService formulaService;

    @Override
    public ApiResponseDTO<ClaimCalculationResponseDTO> calculateBenefit(
            ClaimInitialPreviewRequest request
    ) {
        MemberContributionSummary contributionSummary =
                memberContributionService.getContributionSummary(request.getNppfNumber());

        ApiResponseDTO<List<MatchedConditionRuleDto>> ruleResponse =
                ruleService.playWithRule(request);

        List<MatchedConditionRuleDto> matchedRules = ruleResponse.getData();

        if (matchedRules == null || matchedRules.isEmpty()) {
            return ApiResponseDTO.notFound("No matched rules found");
        }

        List<ComponentBalanceDTO> finalComponents = new ArrayList<>();

        for (MatchedConditionRuleDto matchedRule : matchedRules) {
            finalComponents.addAll(
                    getMatchedComponentsForRule(
                            matchedRule,
                            request,
                            contributionSummary
                    )
            );
        }

        BigDecimal totalPfAmount = sumByPrefix(finalComponents, "PF_", false);
        BigDecimal totalPensionAmount = sumByPrefix(finalComponents, "PC_", false);
        BigDecimal totalPfInterestAmount = sumByPrefix(finalComponents, "PF_", true);
        BigDecimal totalPensionInterestAmount = sumByPrefix(finalComponents, "PC_", true);

        BigDecimal serviceYears = calculateServiceYears(
                contributionSummary.getContributionStartDate(),
                contributionSummary.getContributionEndDate()
        );

        ClaimCalculationResponseDTO response =
                ClaimCalculationResponseDTO.builder()
                        .nppfNumber(contributionSummary.getNppfNumber())
                        .contributionStartDate(contributionSummary.getContributionStartDate())
                        .contributionEndDate(contributionSummary.getContributionEndDate())
                        .totalContributionMonths(contributionSummary.getTotalContributionMonths())
                        .totalNonContributionMonths(contributionSummary.getTotalNonContributionMonths())
                        .noOfYearInService(serviceYears)
                        .components(finalComponents)
                        .totalPfAmount(totalPfAmount)
                        .totalPensionAmount(totalPensionAmount)
                        .totalPfInterestAmount(totalPfInterestAmount)
                        .totalPensionInterestAmount(totalPensionInterestAmount)
                        .pfIsEligible(totalPfAmount.compareTo(BigDecimal.ZERO) > 0
                                ? EligibilityEnum.ELIGIBLE
                                : EligibilityEnum.NOT_ELIGIBLE)
                        .pensionIsEligible(totalPensionAmount.compareTo(BigDecimal.ZERO) > 0
                                ? EligibilityEnum.ELIGIBLE
                                : EligibilityEnum.NOT_ELIGIBLE)
                        .build();

        return ApiResponseDTO.success(response);
    }

    private List<ComponentBalanceDTO> getMatchedComponentsForRule(
            MatchedConditionRuleDto matchedRule,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary
    ) {
        Long subRuleId = matchedRule.getSubRuleId();

        Long conditionId = matchedRule.getCondition() != null
                ? matchedRule.getCondition().getId()
                : null;

        String categoryId = request.getMemberCategoryId();

        ClaimFormulaResponseDto formula =
                formulaService.getBySubRuleId(subRuleId, conditionId, categoryId);

        List<MatchedConditionRuleDto.Components> ruleComponents =
                matchedRule.getComponents() == null
                        ? List.of()
                        : matchedRule.getComponents();

        List<FormulaComponentMapResponseDto> formulaComponents =
                formula == null || formula.getFormulaComponents() == null
                        ? List.of()
                        : formula.getFormulaComponents();

        Set<String> ruleComponentNames =
                ruleComponents.stream()
                        .map(MatchedConditionRuleDto.Components::getComponentName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        Set<String> formulaComponentNames =
                formulaComponents.stream()
                        .map(FormulaComponentMapResponseDto::getComponentName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

        if (contributionSummary.getComponentGroups() == null) {
            return List.of();
        }

        return contributionSummary.getComponentGroups()
                .stream()
                .filter(c ->
                        ruleComponentNames.contains(c.getName())
                                && formulaComponentNames.contains(c.getName()))
                .map(this::toComponentBalance)
                .toList();
    }

    private ComponentBalanceDTO toComponentBalance(
            MemberContributionSummary.ComponentGroup component
    ) {
        return ComponentBalanceDTO.builder()
                .code(component.getCode())
                .name(component.getName())
                .type(component.getCode() != null && component.getCode().contains("I")
                        ? "INTEREST"
                        : "CONTRIBUTION")
                .amount(component.getPrincipal())
                .build();
    }

    private BigDecimal sumByPrefix(
            List<ComponentBalanceDTO> components,
            String prefix,
            boolean interest
    ) {
        return components.stream()
                .filter(c -> c.getCode() != null && c.getCode().startsWith(prefix))
                .filter(c -> interest == c.getCode().contains("I"))
                .map(ComponentBalanceDTO::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateServiceYears(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);

        return BigDecimal.valueOf(days)
                .divide(BigDecimal.valueOf(365), 2, java.math.RoundingMode.HALF_UP);
    }
}