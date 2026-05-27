package com.claim.claim_processing.rule.BenefitCalculation.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.loanAdjustment.dto.LoanAdjustmentResultDto;
import com.claim.claim_processing.integration.loanAdjustment.dto.LoanDeductionDto;
import com.claim.claim_processing.integration.loanAdjustment.dto.LoanDetailResponseDto;
import com.claim.claim_processing.integration.loanAdjustment.service.LoanDetailService;
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
        private final LoanDetailService loanDetailService;
        private final ClaimTypeRuleMapRepository claimTypeRuleMapRepository;

        @Override
        public ApiResponseDTO<ClaimCalculationResponseDTO> calculateBenefit(
                        ClaimInitialPreviewRequest request) {

                MemberContributionSummary contributionSummary = memberContributionService
                                .getContributionSummary(request.getNppfNumber());

                ApiResponseDTO<List<MatchedConditionRuleDto>> ruleResponse = ruleService.playWithRule(request);

                List<MatchedConditionRuleDto> matchedRules = ruleResponse == null || ruleResponse.getData() == null
                                ? List.of()
                                : ruleResponse.getData();

                if (matchedRules.isEmpty()) {
                        return ApiResponseDTO.notFound("No matched rules found");
                }
                List<ClaimTypeRuleMap> claimRuleMaps = claimTypeRuleMapRepository
                                .findByClaimTypeId(request.getClaimTypeId());
                if (claimRuleMaps == null || claimRuleMaps.isEmpty()) {
                        return ApiResponseDTO
                                        .notFound("No claim type rule mapping found for claim type id: "
                                                        + request.getClaimTypeId());

                }

                boolean isLoanApply = Boolean.valueOf(
                                claimRuleMaps.stream()
                                                .filter(Objects::nonNull)
                                                .map(ClaimTypeRuleMap::getRuleType)
                                                .filter(Objects::nonNull)
                                                .map(RuleTypeMaster::getCode)
                                                .filter(Objects::nonNull)
                                                .anyMatch(code -> code.toUpperCase().contains("LOAN_ADJUSTMENT")));

                boolean isRentalApply = Boolean.valueOf(
                                claimRuleMaps.stream()
                                                .filter(Objects::nonNull)
                                                .map(ClaimTypeRuleMap::getRuleType)
                                                .filter(Objects::nonNull)
                                                .map(RuleTypeMaster::getCode)
                                                .filter(Objects::nonNull)
                                                .anyMatch(code -> code.toUpperCase().contains("RENTAL_ADJUSTMENT")));
                List<ComponentBalanceDTO> eligibleComponents = new ArrayList<>();
                List<ComponentBalanceDTO> forfeitedComponents = new ArrayList<>();

                List<String> eligibilityNotes = new ArrayList<>();
                List<String> vestingNotes = new ArrayList<>();
                List<String> recommendedRefundTypes = new ArrayList<>();
                List<String> forfeitedComponentCodes = new ArrayList<>();

                Integer totalMonths = contributionSummary == null
                                ? null
                                : contributionSummary.getTotalContributionMonths();

                System.out.println("\n========== MATCHED RULES ==========");

                matchedRules.forEach(rule -> {

                        System.out.println("--------------------------------");
                        System.out.println("Rule Code      : " + rule.getRuleCode());
                        System.out.println("Rule Name      : " + rule.getRuleName());
                        System.out.println("SubRuleId      : " + rule.getSubRuleId());

                        if (rule.getCondition() != null) {

                                System.out.println("Condition Id   : " + rule.getCondition().getId());
                                System.out.println("Scheme Type Id : " + rule.getCondition().getSchemeTypeId());
                                System.out.println("Min Months     : " + rule.getCondition().getMinMonths());
                                System.out.println("Max Months     : " + rule.getCondition().getMaxMonths());
                                System.out.println("Comparison     : " + rule.getCondition().getComparisonType());
                        }

                        System.out.println("---------- RULE COMPONENTS ----------");

                        if (rule.getComponents() != null && !rule.getComponents().isEmpty()) {

                                rule.getComponents().forEach(component -> {

                                        System.out.println(
                                                        "Component -> "
                                                                        + "id=" + component.getComponentId()
                                                                        + ", code=" + component.getComponentCode()
                                                                        + ", name=" + component.getComponentName());
                                });

                        } else {
                                System.out.println("No rule components");
                        }

                        System.out.println("---------- REFUND TYPES ----------");

                        if (rule.getRefundTypes() != null && !rule.getRefundTypes().isEmpty()) {

                                rule.getRefundTypes().forEach(refund -> {

                                        System.out.println(
                                                        "Refund -> "
                                                                        + "id=" + refund.getId()
                                                                        + ", name=" + refund.getName());
                                });

                        } else {
                                System.out.println("No refund types");
                        }
                });

                System.out.println("====================================\n");
                for (MatchedConditionRuleDto matchedRule : matchedRules) {

                        if (matchedRule == null) {
                                continue;
                        }

                        String ruleCode = safeUpper(matchedRule.getRuleCode());

                        // 1. PARTIAL WITHDRAWAL
                        if (isPartialWithdrawalRule(matchedRule)) {
                                ComponentBalanceDTO partialComponent = calculatePartialWithdrawal(matchedRule,
                                                contributionSummary);

                                if (partialComponent != null) {
                                        eligibleComponents.add(partialComponent);

                                        eligibilityNotes.add(
                                                        buildPartialWithdrawalNote(
                                                                        matchedRule,
                                                                        partialComponent,
                                                                        totalMonths));
                                }

                                continue;
                        }

                        // 2. VESTING
                        if (isVestingRule(ruleCode)) {
                                List<String> refundNames = getRefundTypeNames(matchedRule);

                                recommendedRefundTypes.addAll(refundNames);

                                if (!refundNames.isEmpty()) {
                                        vestingNotes.add(buildVestingNote(matchedRule, refundNames));
                                }

                                continue;
                        }

                        // 3. LAPSED
                        if (isLapsedRule(ruleCode)) {

                                List<ComponentBalanceDTO> forfeited = getRuleAmountUsingFormulaIfAvailable(
                                                matchedRule,
                                                request,
                                                contributionSummary,
                                                "FORFEITED");

                                if (forfeited.isEmpty()) {
                                        forfeited = getComponentsFromRule(matchedRule, contributionSummary);
                                }

                                forfeitedComponents.addAll(forfeited);

                                forfeitedComponentCodes.addAll(
                                                forfeited.stream()
                                                                .map(ComponentBalanceDTO::getCode)
                                                                .filter(Objects::nonNull)
                                                                .map(this::normalizeCode)
                                                                .distinct()
                                                                .toList());

                                continue;
                        }

                        // 4. NORMAL ELIGIBILITY
                        List<ComponentBalanceDTO> eligible = getRuleAmountUsingFormulaIfAvailable(
                                        matchedRule,
                                        request,
                                        contributionSummary,
                                        "ELIGIBLE");

                        eligibleComponents.addAll(eligible);

                        if (!eligible.isEmpty()) {
                                eligibilityNotes.add(
                                                buildEligibilityNote(matchedRule, eligible, totalMonths));
                        }
                }
                List<ComponentBalanceDTO> finalComponents = eligibleComponents.stream()
                                .filter(Objects::nonNull)
                                .toList();

                BigDecimal totalPfAmount = sumByPrefix(finalComponents, "PF_", false);
                BigDecimal totalPensionAmount = sumByPrefix(finalComponents, "PC_", false);
                BigDecimal totalPfInterestAmount = sumByPrefix(finalComponents, "PF_", true);
                BigDecimal totalPensionInterestAmount = sumByPrefix(finalComponents, "PC_", true);

                LoanAdjustmentResultDto loanAdjustmentResult = null;

                if (isLoanApply) {
                        loanAdjustmentResult = deductLoanByPriority(
                                        request.getNppfNumber(),
                                        matchedRules,
                                        finalComponents);
                }
                BigDecimal serviceYears = contributionSummary == null
                                ? BigDecimal.ZERO
                                : calculateServiceYears(
                                                contributionSummary.getContributionStartDate(),
                                                contributionSummary.getContributionEndDate());

                ClaimCalculationResponseDTO response = ClaimCalculationResponseDTO.builder()
                                .nppfNumber(contributionSummary != null ? contributionSummary.getNppfNumber() : null)
                                .contributionStartDate(
                                                contributionSummary != null
                                                                ? contributionSummary.getContributionStartDate()
                                                                : null)
                                .contributionEndDate(contributionSummary != null
                                                ? contributionSummary.getContributionEndDate()
                                                : null)
                                .totalContributionMonths(
                                                contributionSummary != null
                                                                ? contributionSummary.getTotalContributionMonths()
                                                                : null)
                                .totalNonContributionMonths(
                                                contributionSummary != null
                                                                ? contributionSummary.getTotalNonContributionMonths()
                                                                : null)
                                .noOfYearInService(serviceYears)
                                .components(finalComponents)
                                .loanCheck(isLoanApply)
                                .rentalCheck(isRentalApply)
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
                                .eligibilityNote(String.join(" ", eligibilityNotes)
                                                + (vestingNotes.isEmpty()
                                                                ? ""
                                                                : " Vesting Notes: " + String.join(" ", vestingNotes)))
                                .vestingNote(String.join(" ", vestingNotes))
                                .recommendedBenefitType(String.join(" ", recommendedRefundTypes))
                                .finalPayableAmount(loanAdjustmentResult.getFinalPayableAmount())
                                .adjustmentNote(loanAdjustmentResult.getAdjustmentNote())
                                .forfeitedComponents(forfeitedComponentCodes)

                                .build();

                return ApiResponseDTO.success(response);
        }

        private LoanAdjustmentResultDto deductLoanByPriority(
                        String memberCode,
                        List<MatchedConditionRuleDto> matchedRules,
                        List<ComponentBalanceDTO> finalComponents) {
                BigDecimal grossEligibleAmount = finalComponents == null
                                ? BigDecimal.ZERO
                                : finalComponents.stream()
                                                .filter(Objects::nonNull)
                                                .map(ComponentBalanceDTO::getAmount)
                                                .filter(Objects::nonNull)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal remainingEligibleAmount = grossEligibleAmount;

                ApiResponseDTO<List<LoanDetailResponseDto>> loanResponse = loanDetailService.getLoanDetails(memberCode);

                List<LoanDetailResponseDto> loanDetails = loanResponse == null || loanResponse.getData() == null
                                ? List.of()
                                : loanResponse.getData();

                if (loanDetails.isEmpty()) {
                        return LoanAdjustmentResultDto.builder()
                                        .finalPayableAmount(grossEligibleAmount)
                                        .adjustmentNote("No loan found for adjustment.")
                                        .build();
                }

                List<LoanDeductionDto> loanRules = new ArrayList<>();

                for (MatchedConditionRuleDto rule : matchedRules) {
                        if (rule == null || rule.getCondition() == null) {
                                continue;
                        }

                        if (!Boolean.TRUE.equals(rule.getIsLoanRule())) {
                                continue;
                        }

                        String loanTypeName = rule.getLoanType() != null
                                        ? rule.getLoanType()
                                        : "Unknown";

                        BigDecimal outstandingAmount = findLoanOutstandingAmount(
                                        loanDetails,
                                        loanTypeName);

                        loanRules.add(
                                        LoanDeductionDto.builder()
                                                        .loanTypeName(loanTypeName)
                                                        .prioritySequence(rule.getCondition().getPriorityOrder())
                                                        .outstandingAmount(outstandingAmount)
                                                        .build());
                }

                if (loanRules.isEmpty()) {
                        return LoanAdjustmentResultDto.builder()
                                        .finalPayableAmount(grossEligibleAmount)
                                        .adjustmentNote("Loan adjustment is applicable, but no loan rule was matched.")
                                        .build();
                }

                loanRules.sort(Comparator.comparing(
                                LoanDeductionDto::getPrioritySequence,
                                Comparator.nullsLast(Comparator.naturalOrder())));

                List<String> adjustmentNotes = new ArrayList<>();

                for (LoanDeductionDto loan : loanRules) {
                        BigDecimal outstandingAmount = nullSafe(loan.getOutstandingAmount());

                        if (outstandingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                                adjustmentNotes.add(loan.getLoanTypeName() + " loan has no outstanding amount.");
                                continue;
                        }

                        if (remainingEligibleAmount.compareTo(BigDecimal.ZERO) <= 0) {
                                adjustmentNotes.add(loan.getLoanTypeName()
                                                + " loan was not adjusted due to insufficient eligible amount.");
                                continue;
                        }

                        BigDecimal adjustedAmount = outstandingAmount.min(remainingEligibleAmount);
                        BigDecimal remainingLoanAmount = outstandingAmount.subtract(adjustedAmount);

                        remainingEligibleAmount = remainingEligibleAmount.subtract(adjustedAmount);

                        if (remainingLoanAmount.compareTo(BigDecimal.ZERO) == 0) {
                                adjustmentNotes.add(loan.getLoanTypeName() + " loan was fully adjusted with amount "
                                                + adjustedAmount);
                        } else {
                                adjustmentNotes.add(loan.getLoanTypeName() + " loan was partially adjusted with amount "
                                                + adjustedAmount + ". Remaining loan balance is "
                                                + remainingLoanAmount);
                        }
                }

                return LoanAdjustmentResultDto.builder()
                                .finalPayableAmount(remainingEligibleAmount)
                                .adjustmentNote(String.join(". ", adjustmentNotes)
                                                + ". Final payable amount is " + remainingEligibleAmount + ".")
                                .build();
        }

        private BigDecimal findLoanOutstandingAmount(
                        List<LoanDetailResponseDto> loanDetails,
                        String loanTypeName) {
                if (loanDetails == null || loanTypeName == null) {
                        return BigDecimal.ZERO;
                }

                return loanDetails.stream()
                                .filter(Objects::nonNull)
                                .filter(loan -> loan.getLoanName() != null)
                                .filter(loan -> loanTypeName.equalsIgnoreCase(loan.getLoanName()))
                                .map(LoanDetailResponseDto::getOutstandingAmount)
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        public ApiResponseDTO<BigDecimal> getFinalAmountAfterDeduction(BigDecimal calculatedAmount,
                        Boolean isLoanThere) {

                return null;
        }

        private List<ComponentBalanceDTO> getRuleAmountUsingFormulaIfAvailable(
                        MatchedConditionRuleDto matchedRule,
                        ClaimInitialPreviewRequest request,
                        MemberContributionSummary contributionSummary,
                        String resultType) {

                if (!hasComponents(matchedRule)
                                || contributionSummary == null
                                || contributionSummary.getComponentGroups() == null) {
                        return List.of();
                }

                Long conditionId = matchedRule.getCondition() != null
                                ? matchedRule.getCondition().getId()
                                : null;

                ClaimFormulaResponseDto formula;

                try {
                        formula = formulaService.getBySubRuleId(
                                        matchedRule.getSubRuleId(),
                                        conditionId,
                                        request.getMemberCategoryId());
                } catch (RuntimeException ex) {
                        return List.of();
                }

                if (formula == null || formula.getFormulaComponents() == null) {
                        return List.of();
                }

                Set<String> ruleCodes = getRuleComponentCodes(matchedRule)
                                .stream()
                                .map(this::normalizeCode)
                                .collect(Collectors.toSet());

                Set<String> formulaCodes = formula.getFormulaComponents()
                                .stream()
                                .map(FormulaComponentMapResponseDto::getVariableCode)
                                .filter(Objects::nonNull)
                                .map(this::normalizeCode)
                                .collect(Collectors.toSet());

                Set<String> matchedCodes = ruleCodes.stream()
                                .filter(formulaCodes::contains)
                                .collect(Collectors.toSet());

                if (matchedCodes.isEmpty()) {
                        return List.of();
                }

                List<ComponentBalanceDTO> matchedComponents = contributionSummary.getComponentGroups()
                                .stream()
                                .filter(Objects::nonNull)
                                .filter(c -> c.getCode() != null)
                                .filter(c -> matchedCodes.contains(normalizeCode(c.getCode())))
                                .map(this::toComponentBalance)
                                .toList();

                BigDecimal formulaAmount = evaluateFormulaExpression(
                                formula.getExpressionText(),
                                matchedComponents);

                if (formulaAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        return List.of();
                }

                return List.of(
                                ComponentBalanceDTO.builder()
                                                .code(normalizeCode(formula.getFormulaCode()))
                                                .name(formula.getFormulaName())
                                                .type(resultType)
                                                .amount(formulaAmount)
                                                .build());
        }

        private List<ComponentBalanceDTO> getComponentsFromRule(
                        MatchedConditionRuleDto matchedRule,
                        MemberContributionSummary contributionSummary) {

                if (!hasComponents(matchedRule)
                                || contributionSummary == null
                                || contributionSummary.getComponentGroups() == null) {
                        return List.of();
                }

                System.out.println("========== LAPSED RULE COMPONENTS ==========");
                matchedRule.getComponents()
                                .forEach(c -> System.out.println(c.getComponentCode() + " - " + c.getComponentName()));

                Set<String> ruleCodes = getRuleComponentCodes(matchedRule)
                                .stream()
                                .map(this::normalizeCode)
                                .collect(Collectors.toSet());

                List<ComponentBalanceDTO> components = contributionSummary.getComponentGroups()
                                .stream()
                                .filter(Objects::nonNull)
                                .filter(c -> c.getCode() != null)
                                .filter(c -> ruleCodes.contains(normalizeCode(c.getCode())))
                                .map(this::toComponentBalance)
                                .toList();

                System.out.println("========== MATCHED LAPSED COMPONENTS ==========");
                components.forEach(c -> System.out
                                .println(c.getCode() + " - " + c.getName() + " amount=" + c.getAmount()));

                return components;
        }

        private BigDecimal evaluateFormulaExpression(
                        String expressionText,
                        List<ComponentBalanceDTO> components) {

                if (expressionText == null
                                || expressionText.isBlank()
                                || components == null
                                || components.isEmpty()) {
                        return BigDecimal.ZERO;
                }

                String expression = expressionText
                                .replace("(", "")
                                .replace(")", "")
                                .replace(" ", "")
                                .toUpperCase();

                System.out.println("Normalized Expression = " + expression);

                java.util.Map<String, BigDecimal> componentMap = components.stream()
                                .filter(Objects::nonNull)
                                .filter(c -> c.getCode() != null)
                                .collect(Collectors.toMap(
                                                c -> normalizeCode(c.getCode()),
                                                c -> nullSafe(c.getAmount()),
                                                BigDecimal::add));

                BigDecimal result = BigDecimal.ZERO;

                String[] tokens = expression.split("\\+");

                for (String token : tokens) {

                        String normalizedToken = normalizeCode(token);

                        BigDecimal amount = componentMap.getOrDefault(
                                        normalizedToken,
                                        BigDecimal.ZERO);

                        System.out.println(
                                        "Formula Token = "
                                                        + normalizedToken
                                                        + ", Amount = "
                                                        + amount);

                        result = result.add(amount);
                }

                return result;
        }

        private List<String> getRuleComponentCodes(MatchedConditionRuleDto matchedRule) {
                if (matchedRule == null || matchedRule.getComponents() == null) {
                        return List.of();
                }

                return matchedRule.getComponents()
                                .stream()
                                .filter(Objects::nonNull)
                                .map(MatchedConditionRuleDto.Components::getComponentCode)
                                .filter(Objects::nonNull)
                                .map(this::normalizeCode)
                                .distinct()
                                .toList();
        }

        private List<String> getRefundTypeNames(MatchedConditionRuleDto matchedRule) {
                if (matchedRule == null || matchedRule.getRefundTypes() == null) {
                        return List.of();
                }

                return matchedRule.getRefundTypes()
                                .stream()
                                .filter(Objects::nonNull)
                                .map(MatchedConditionRuleDto.RefundTypeDTO::getName)
                                .filter(Objects::nonNull)
                                .distinct()
                                .toList();
        }

        private String buildEligibilityNote(
                        MatchedConditionRuleDto rule,
                        List<ComponentBalanceDTO> eligibleComponents,
                        Integer totalMonths) {

                String componentCodes = eligibleComponents.stream()
                                .map(ComponentBalanceDTO::getCode)
                                .filter(Objects::nonNull)
                                .map(this::normalizeCode)
                                .distinct()
                                .collect(Collectors.joining(", "));

                return "You have " + totalMonths
                                + " months contribution. Eligible under '"
                                + rule.getRuleName()
                                + "'. Benefits: " + componentCodes + ".";
        }

        private String buildVestingNote(
                        MatchedConditionRuleDto rule,
                        List<String> refundNames) {

                return "Vesting rule '" + rule.getRuleName()
                                + "' matched. Benefit type: "
                                + String.join(", ", refundNames) + ".";
        }

        private ComponentBalanceDTO toComponentBalance(
                        MemberContributionSummary.ComponentGroup component) {

                String code = normalizeCode(component.getCode());

                boolean interestComponent = code.contains("I");

                return ComponentBalanceDTO.builder()
                                .code(code)
                                .name(component.getName())
                                .type(interestComponent ? "INTEREST" : "CONTRIBUTION")
                                .amount(interestComponent
                                                ? nullSafe(component.getInterest())
                                                : nullSafe(component.getPrincipal()))
                                .build();
        }

        private BigDecimal sumByPrefix(
                        List<ComponentBalanceDTO> components,
                        String prefix,
                        boolean interest) {

                if (components == null || components.isEmpty()) {
                        return BigDecimal.ZERO;
                }

                return components.stream()
                                .filter(Objects::nonNull)
                                .filter(c -> c.getCode() != null)
                                .filter(c -> c.getCode().startsWith(prefix))
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
                                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        }

        private boolean hasComponents(MatchedConditionRuleDto matchedRule) {
                return matchedRule != null
                                && matchedRule.getComponents() != null
                                && !matchedRule.getComponents().isEmpty();
        }

        private boolean isVestingRule(String ruleCode) {
                return ruleCode != null && ruleCode.startsWith("VESTING_");
        }

        private boolean isLapsedRule(String ruleCode) {
                return ruleCode != null && ruleCode.startsWith("LAPSED_");
        }

        private String safeUpper(String value) {
                return value == null ? "" : value.toUpperCase();
        }

        private String normalizeCode(String value) {
                return value == null ? "" : value.trim().toUpperCase();
        }

        private BigDecimal nullSafe(BigDecimal value) {
                return value == null ? BigDecimal.ZERO : value;
        }

        private boolean isPartialWithdrawalRule(MatchedConditionRuleDto rule) {
                return rule != null
                                && rule.getCondition() != null
                                && rule.getCondition().getWithdrawalPercentage() != null
                                && rule.getCondition().getAccumulation() != null;
        }

        private ComponentBalanceDTO calculatePartialWithdrawal(
                        MatchedConditionRuleDto rule,
                        MemberContributionSummary contributionSummary) {

                if (rule == null || rule.getCondition() == null
                                || contributionSummary == null
                                || contributionSummary.getComponentGroups() == null) {
                        return null;
                }

                Double percentage = rule.getCondition().getWithdrawalPercentage();

                if (percentage == null || percentage.compareTo(0.0) <= 0) {
                        return null;
                }

                String accumulationCode = rule.getCondition().getAccumulation().getCode();
                String accumulationName = rule.getCondition().getAccumulation().getName();

                BigDecimal accumulationAmount = calculateAccumulationAmount(accumulationCode, contributionSummary);

                if (accumulationAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        return null;
                }

                BigDecimal payableAmount = accumulationAmount
                                .multiply(BigDecimal.valueOf(percentage))
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                return ComponentBalanceDTO.builder()
                                .code(normalizeCode(accumulationCode))
                                .name(accumulationName)
                                .type("PARTIAL_WITHDRAWAL")
                                .amount(payableAmount)
                                .build();
        }

        private BigDecimal calculateAccumulationAmount(
                        String accumulationCode,
                        MemberContributionSummary contributionSummary) {

                if (accumulationCode == null || contributionSummary.getComponentGroups() == null) {
                        return BigDecimal.ZERO;
                }

                String code = normalizeCode(accumulationCode);

                return contributionSummary.getComponentGroups()
                                .stream()
                                .filter(Objects::nonNull)
                                .filter(component -> {
                                        String componentCode = normalizeCode(component.getCode());

                                        if ("TOTAL_PF_ACCUMULATION".equals(code)) {
                                                return componentCode.startsWith("PF_");
                                        }

                                        if ("TOTAL_ACCUMULATION".equals(code)) {
                                                return true;
                                        }

                                        return false;
                                })
                                .map(this::componentTotalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private BigDecimal componentTotalAmount(
                        MemberContributionSummary.ComponentGroup component) {

                if (component == null) {
                        return BigDecimal.ZERO;
                }

                if (component.getTotalBalance() != null) {
                        return component.getTotalBalance();
                }

                return nullSafe(component.getPrincipal())
                                .add(nullSafe(component.getInterest()));
        }

        private String buildPartialWithdrawalNote(
                        MatchedConditionRuleDto rule,
                        ComponentBalanceDTO component,
                        Integer totalMonths) {

                Double percentage = rule.getCondition().getWithdrawalPercentage();

                return "You have " + totalMonths
                                + " months contribution. Partial withdrawal rule '"
                                + rule.getRuleName()
                                + "' matched. Eligible for "
                                + percentage
                                + "% of "
                                + component.getCode()
                                + ". Payable amount: "
                                + component.getAmount()
                                + ".";
        }
}