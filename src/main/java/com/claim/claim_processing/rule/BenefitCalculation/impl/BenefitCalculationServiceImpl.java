package com.claim.claim_processing.rule.BenefitCalculation.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.loanAdjustment.dto.LoanAdjustmentResultDto;
import com.claim.claim_processing.integration.loanAdjustment.dto.LoanDetailResponseDto;
import com.claim.claim_processing.integration.loanAdjustment.service.LoanDetailService;
import com.claim.claim_processing.rule.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.claim.DTO.response.EligibilityResultDto;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedResultDto;
import com.claim.claim_processing.rule.claim.DTO.response.LoanAdjustmentDetailDto;
import com.claim.claim_processing.rule.claim.DTO.response.VestingResultDto;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleGateWay.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleGateWay.entities.rule.LoanDeductionMapping;
import com.claim.claim_processing.rule.ruleGateWay.repositories.rule.LoanDeductionMappingRepository;
import com.claim.claim_processing.rule.ruleProcessing.service.PartialWithdrawalRuleService;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BenefitCalculationServiceImpl implements BenefitCalculationService {

    private final MemberContributionService memberContributionService;
    private final RuleService ruleService;
    private final PartialWithdrawalRuleService partialWithdrawalRuleService;
    private final LoanDetailService loanDetailService;
    private final ClaimTypeRuleMapRepository claimTypeRuleMapRepository;
    private final LoanDeductionMappingRepository loanDeductionMappingRepository;

    @Override
    public ApiResponseDTO<ClaimCalculationResponseDTO> calculateBenefit(
            ClaimInitialPreviewRequest request) {
        if (isPartialWithdrawalRule(request.getClaimTypeId())) {
            return partialWithdrawalRuleService.calculatePartialWithdrawal(request);
        }
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(request.getNppfNumber());

        ApiResponseDTO<List<MatchedSubClaimRuleDto>> ruleResponse = ruleService.playWithRule(request);

        List<MatchedSubClaimRuleDto> matchedRules = ruleResponse == null || ruleResponse.getData() == null
                ? List.of()
                : ruleResponse.getData();
        System.out.println("check the rule: " + ruleResponse);
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

        String vestingNote = "";
        List<String> recommendedRefundTypes = new ArrayList<>();
        List<String> forfeitedComponentCodes = new ArrayList<>();

        Integer totalMonths = contributionSummary == null
                ? null
                : contributionSummary.getTotalContributionMonths();
        List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations = new ArrayList<>();

        for (MatchedSubClaimRuleDto matchedRule : matchedRules) {

            if (matchedRule == null) {
                continue;
            }
            printMatchedRuleDebug(matchedRule);

            String ruleCode = safeUpper(matchedRule.getRuleCode());

            if (isVestingRule(ruleCode)) {

                VestingResultDto vestingResult = handleVestingRule(matchedRule);

                if (vestingResult != null && vestingResult.isLumpSumEligible()) {

                    if (vestingResult.getRefundTypeName() != null
                            && !vestingResult.getRefundTypeName().isBlank()) {
                        vestingNote = "Vesting rule matched. Recommended benefit type: "
                                + vestingResult.getRefundTypeName();
                    }

                }

                continue;
            }

            if (isLapsedRule(ruleCode)) {

                LapsedResultDto lapsedResult = handleLapsedRule(
                        matchedRule,
                        request,
                        contributionSummary, expressionCalculations);

                if (lapsedResult != null && lapsedResult.isForfeited()) {
                    forfeitedComponents.addAll(lapsedResult.getForfeitedComponents());
                    forfeitedComponentCodes.addAll(lapsedResult.getForfeitedComponentCodes());
                }

                continue;
            }

            EligibilityResultDto eligibilityResult = handleEligibilityRule(
                    matchedRule,
                    request,
                    contributionSummary, expressionCalculations);

            if (eligibilityResult != null && eligibilityResult.getEligibleComponents() != null) {
                eligibleComponents.addAll(eligibilityResult.getEligibleComponents());
            }
        }
        List<ComponentBalanceDTO> finalComponents = eligibleComponents.stream()
                .filter(Objects::nonNull)
                .toList();

        BigDecimal totalPfAmount = BigDecimal.ZERO;
        BigDecimal backUpTotalPfAmount = BigDecimal.ZERO;
        BigDecimal totalPensionAmount = BigDecimal.ZERO;
        BigDecimal backupTotalPensionAmount = BigDecimal.ZERO;
        BigDecimal totalPfInterestAmount = BigDecimal.ZERO;
        BigDecimal backupTotalPfInterestAmount = BigDecimal.ZERO;
        BigDecimal totalPensionInterestAmount = BigDecimal.ZERO;
        BigDecimal backupTotalPensionInterestAmount = BigDecimal.ZERO;

        for (ComponentBalanceDTO component : finalComponents) {

            if (component.getCode() == null) {
                continue;
            }

            String code = component.getCode().trim().toUpperCase();

            BigDecimal amount = component.getAmount() == null
                    ? BigDecimal.ZERO
                    : component.getAmount();

            switch (code) {
                case "PF_MC":
                case "PF_EC":
                case "PF_GC":
                case "PF_VC":
                    totalPfAmount = totalPfAmount.add(amount);
                    backUpTotalPfAmount = backUpTotalPfAmount.add(amount);
                    break;

                case "PF_IMC":
                case "PF_IEC":
                case "PF_GIC":
                case "PF_VIC":
                    totalPfInterestAmount = totalPfInterestAmount.add(amount);
                    backupTotalPfInterestAmount = backupTotalPfInterestAmount.add(amount);
                    break;

                case "PC_MC":
                case "PC_EC":
                case "PC_GC":
                case "PC_VC":
                    totalPensionAmount = totalPensionAmount.add(amount);
                    backupTotalPensionAmount = backupTotalPensionAmount.add(amount);
                    break;

                case "PC_IMC":
                case "PC_IEC":
                case "PC_GIC":
                case "PC_VIC":
                    totalPensionInterestAmount = totalPensionInterestAmount.add(amount);
                    backupTotalPensionInterestAmount = backupTotalPensionInterestAmount.add(amount);
                    break;

                default:
                    break;
            }
        }

        BigDecimal grossPayableAmount = finalComponents.stream()
                .filter(Objects::nonNull)
                .map(ComponentBalanceDTO::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LoanAdjustmentResultDto loanAdjustmentResult = null;

        BigDecimal finalPayableAmount = grossPayableAmount;

        if (isLoanApply) {

            loanAdjustmentResult = deductLoanByPriority(
                    request.getNppfNumber(),
                    finalComponents);

            if (loanAdjustmentResult != null) {
                finalPayableAmount = loanAdjustmentResult.getFinalPayableAmount();
            }
        }

        BigDecimal serviceYears = contributionSummary == null
                ? BigDecimal.ZERO
                : calculateServiceYears(
                        contributionSummary.getContributionStartDate(),
                        contributionSummary.getContributionEndDate());

        String eligibilityNote = buildEligibilityPreviewNote(
                finalComponents,
                totalPfAmount,
                totalPensionAmount);

        String loanNote = loanAdjustmentResult != null
                ? loanAdjustmentResult.getAdjustmentNote()
                : "No loan adjustment applied.";
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
                .expressionCalculations(expressionCalculations)
                .loanCheck(isLoanApply)
                .rentalCheck(isRentalApply)
                .totalPfAmount(backUpTotalPfAmount)
                .totalPensionAmount(backupTotalPensionAmount)
                .totalPfInterestAmount(backupTotalPfInterestAmount)
                .totalPensionInterestAmount(backupTotalPensionInterestAmount)
                .pfIsEligible(backUpTotalPfAmount.compareTo(BigDecimal.ZERO) > 0
                        ? EligibilityEnum.ELIGIBLE
                        : EligibilityEnum.NOT_ELIGIBLE)
                .pensionIsEligible(backupTotalPensionAmount.compareTo(BigDecimal.ZERO) > 0
                        ? EligibilityEnum.ELIGIBLE
                        : EligibilityEnum.NOT_ELIGIBLE)
                .eligibilityNote(eligibilityNote)
                .vestingNote(vestingNote)
                .recommendedBenefitType(String.join(" ", recommendedRefundTypes))
                .finalPayableAmount(finalPayableAmount)
                .adjustmentNote(loanNote)
                .forfeitedComponents(forfeitedComponentCodes)

                .build();

        return ApiResponseDTO.success(response);
    }

    private String buildEligibilityPreviewNote(
            List<ComponentBalanceDTO> finalComponents,
            BigDecimal totalPfAmount,
            BigDecimal totalPensionAmount) {

        if (finalComponents == null || finalComponents.isEmpty()) {
            return "No eligible components found.";
        }

        String components = finalComponents.stream()
                .filter(Objects::nonNull)
                .map(ComponentBalanceDTO::getCode)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));

        return "Eligible components: "
                + components
                + ". PF Amount: "
                + totalPfAmount
                + ", Pension Amount: "
                + totalPensionAmount
                + ".";
    }

    private BigDecimal calculateServiceYears(
            LocalDate contributionStartDate,
            LocalDate contributionEndDate) {

        if (contributionStartDate == null || contributionEndDate == null) {
            return BigDecimal.ZERO;
        }

        if (contributionEndDate.isBefore(contributionStartDate)) {
            return BigDecimal.ZERO;
        }

        long totalMonths = ChronoUnit.MONTHS.between(
                contributionStartDate,
                contributionEndDate);

        return BigDecimal.valueOf(totalMonths)
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }

    private EligibilityResultDto handleEligibilityRule(
            MatchedSubClaimRuleDto matchedRule,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary,
            List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

        List<ComponentBalanceDTO> eligible = getRuleAmountUsingFormulaIfAvailable(
                matchedRule,
                request,
                contributionSummary,
                "ELIGIBLE", expressionCalculations);

        if (eligible == null || eligible.isEmpty()) {

            eligible = getComponentsFromRule(
                    matchedRule,
                    contributionSummary);
        }

        return EligibilityResultDto.builder()
                .eligibleComponents(
                        eligible == null
                                ? Collections.emptyList()
                                : eligible)
                .build();
    }

    private LoanAdjustmentResultDto deductLoanByPriority(
            String nppfNumber,
            List<ComponentBalanceDTO> finalComponents) {

        BigDecimal grossPayableAmount = finalComponents == null
                ? BigDecimal.ZERO
                : finalComponents.stream()
                        .filter(Objects::nonNull)
                        .map(ComponentBalanceDTO::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingPayableAmount = grossPayableAmount;
        BigDecimal totalLoanOutstandingAmount = BigDecimal.ZERO;
        BigDecimal totalLoanAdjustedAmount = BigDecimal.ZERO;

        List<LoanAdjustmentDetailDto> adjustmentDetails = new ArrayList<>();

        List<LoanDetailResponseDto> loanDetails = loanDetailService.getLoanDetails(nppfNumber).getData();

        if (loanDetails == null || loanDetails.isEmpty()) {

            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(grossPayableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("No outstanding loan found.")
                    .build();
        }

        List<LoanDeductionMapping> mappings = loanDeductionMappingRepository.findAll();

        Map<Long, Integer> priorityMap = mappings.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        mapping -> mapping.getLoanType().getId(),
                        LoanDeductionMapping::getPriorityOrder));

        List<LoanDetailResponseDto> sortedLoanDetails = loanDetails.stream()
                .filter(Objects::nonNull)
                .filter(loan -> loan.getOutstandingAmount() != null)
                .filter(loan -> loan.getOutstandingAmount().compareTo(BigDecimal.ZERO) > 0)
                .sorted(
                        Comparator.comparing(
                                loan -> priorityMap.getOrDefault(
                                        loan.getLoanId(),
                                        Integer.MAX_VALUE)))
                .toList();

        for (LoanDetailResponseDto loan : sortedLoanDetails) {

            if (remainingPayableAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal outstandingAmount = loan.getOutstandingAmount();

            totalLoanOutstandingAmount = totalLoanOutstandingAmount.add(outstandingAmount);

            BigDecimal adjustedAmount = remainingPayableAmount.min(outstandingAmount);

            BigDecimal remainingOutstandingAmount = outstandingAmount.subtract(adjustedAmount);

            remainingPayableAmount = remainingPayableAmount.subtract(adjustedAmount);

            totalLoanAdjustedAmount = totalLoanAdjustedAmount.add(adjustedAmount);

            adjustmentDetails.add(
                    LoanAdjustmentDetailDto.builder()
                            .loanTypeId(loan.getLoanId())
                            .loanTypeName(loan.getLoanName())
                            .priorityOrder(priorityMap.getOrDefault(loan.getLoanId(), Integer.MAX_VALUE))
                            .outstandingAmount(outstandingAmount)
                            .adjustedAmount(adjustedAmount)
                            .remainingOutstandingAmount(remainingOutstandingAmount)
                            .status(
                                    remainingOutstandingAmount.compareTo(BigDecimal.ZERO) == 0
                                            ? "FULLY_ADJUSTED"
                                            : "PARTIALLY_ADJUSTED")
                            .build());
        }

        return LoanAdjustmentResultDto.builder()
                .totalAdjustedAmount(totalLoanAdjustedAmount)
                .finalPayableAmount(remainingPayableAmount)
                .deductions(adjustmentDetails)
                .adjustmentNote(
                        "Loan adjusted by priority. Total adjusted amount: "
                                + totalLoanAdjustedAmount
                                + ". Final payable amount: "
                                + remainingPayableAmount)
                .build();
    }

    private void printMatchedRuleDebug(MatchedSubClaimRuleDto matchedRule) {

        System.out.println("--------------------------------");
        System.out.println("Rule Code      : " + matchedRule.getRuleCode());
        System.out.println("Rule Name      : " + matchedRule.getRuleName());
        System.out.println("SubRuleId      : " + matchedRule.getSubClaimMappingId());

        if (matchedRule.getCondition() != null) {
            System.out.println("Condition Code : " + matchedRule.getCondition().getConditionCode());
            System.out.println("Condition Check: " + matchedRule.getCondition().getConditionCheck());
            System.out.println("Expression     : " + matchedRule.getCondition().getExpression());
            System.out.println("Duration       : " + matchedRule.getCondition().getDuration());
        }

        if (matchedRule.getComponentMapping() != null) {
            System.out.println("Component Mapping Code : "
                    + matchedRule.getComponentMapping().getComponentMappingCode());
        } else {
            System.out.println("No component mapping");
        }

        System.out.println("---------- REFUND TYPES ----------");

        // if (matchedRule.getRefundTypes() != null &&
        // !matchedRule.getRefundTypes().isEmpty()) {
        // matchedRule.getRefundTypes().forEach(refund ->
        // System.out.println("Refund -> id="
        // + refund.getId()
        // + ", name="
        // + refund.getName())
        // );
        // } else {
        // System.out.println("No refund types");
        // }
    }

    private boolean isLapsedRule(String ruleCode) {
        return ruleCode != null
                && ruleCode.toUpperCase().contains("NORMAL_LAPSED");
    }

    private boolean isVestingRule(String ruleCode) {
        return "VESTING".contains(ruleCode);
    }

    private String safeUpper(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private boolean isPartialWithdrawalRule(Long claimTypeId) {
        return claimTypeRuleMapRepository.findByClaimTypeId(claimTypeId)
                .stream()
                .filter(Objects::nonNull)
                .map(ClaimTypeRuleMap::getRuleType)
                .filter(Objects::nonNull)
                .map(RuleTypeMaster::getCode)
                .filter(Objects::nonNull)
                .anyMatch(code -> code.toUpperCase().contains("PARTIAL"));
    }

    private VestingResultDto handleVestingRule(MatchedSubClaimRuleDto matchedRule) {

        return VestingResultDto.builder()
                .lumpSumEligible(matchedRule.isRefundEligible() ? true : false)
                .refundTypeName(matchedRule.getRefundTypeName())
                .build();

    }

    private LapsedResultDto handleLapsedRule(
            MatchedSubClaimRuleDto matchedRule,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary,
            List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

        List<ComponentBalanceDTO> forfeited = getRuleAmountUsingFormulaIfAvailable(
                matchedRule,
                request,
                contributionSummary,
                "FORFEITED", expressionCalculations);

        if (forfeited == null || forfeited.isEmpty()) {

            forfeited = getComponentsFromRule(
                    matchedRule,
                    contributionSummary);
        }

        if (forfeited == null || forfeited.isEmpty()) {

            return LapsedResultDto.builder()
                    .forfeited(false)
                    .forfeitedComponents(Collections.emptyList())
                    .forfeitedComponentCodes(Collections.emptyList())
                    .build();
        }

        List<String> componentCodes = forfeited.stream()
                .map(ComponentBalanceDTO::getCode)
                .filter(Objects::nonNull)
                .map(code -> code.trim().toUpperCase())
                .distinct()
                .toList();

        return LapsedResultDto.builder()
                .forfeited(true)
                .forfeitedComponents(forfeited)
                .forfeitedComponentCodes(componentCodes)
                .build();
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
        BigDecimal expressionAmount = BigDecimal.ZERO;
        for (MatchedSubClaimRuleDto.ComponentExpression expressionDto : matchedRule.getComponentMapping()
                .getExpressions()) {

            if (expressionDto == null
                    || expressionDto.getExpression() == null
                    || expressionDto.getExpression().isBlank()) {
                continue;
            }

            List<String> resolvedCodes = resolveExpressionComponentCodes(
                    expressionDto.getExpression(),
                    matchedRule.getComponentMapping());

            for (String componentCode : resolvedCodes) {

                BigDecimal amount = componentAmountMap.getOrDefault(
                        componentCode,
                        BigDecimal.ZERO);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                expressionAmount = expressionAmount.add(
                        componentAmountMap.getOrDefault(componentCode, BigDecimal.ZERO));

                results.add(
                        ComponentBalanceDTO.builder()
                                .code(componentCode)
                                .name(componentCode)
                                .type(calculationType)
                                .amount(amount)
                                .build());
            }
            expressionCalculations.add(
                    ClaimCalculationResponseDTO.ExpressionCalculationDTO.builder()
                            .expression(expressionDto.getExpression())
                            .resolvedCodes(resolvedCodes)
                            .expressionAmount(expressionAmount)
                            .type(calculationType)
                            .build());
        }

        return results;
    }

    private List<String> resolveExpressionComponentCodes(
            String expression,
            MatchedSubClaimRuleDto.ComponentMapping mapping) {

        if (expression == null || expression.isBlank() || mapping == null) {
            return Collections.emptyList();
        }

        boolean hasPf = "Y".equalsIgnoreCase(mapping.getHasPf());
        boolean hasPc = "Y".equalsIgnoreCase(mapping.getHasPc());

        String cleanExpression = expression
                .replace(" ", "")
                .toUpperCase();

        String[] tokens = cleanExpression.split("[+\\-]");

        List<String> resolvedCodes = new ArrayList<>();

        for (String token : tokens) {

            if (token == null || token.isBlank()) {
                continue;
            }

            String code = token.trim().toUpperCase();

            if (hasPf) {
                resolvedCodes.add("PF_" + code);
            }

            if (hasPc) {
                resolvedCodes.add("PC_" + code);
            }
        }

        return resolvedCodes.stream()
                .distinct()
                .toList();
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
                    throw new IllegalArgumentException(
                            "Unsupported operator: " + operator);
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

    private List<ComponentBalanceDTO> getComponentsFromRule(
            MatchedSubClaimRuleDto matchedRule,
            MemberContributionSummary contributionSummary) {

        if (matchedRule == null
                || matchedRule.getComponentMapping() == null
                || contributionSummary == null
                || contributionSummary.getComponentGroups() == null) {
            return Collections.emptyList();
        }

        Map<String, BigDecimal> contributionMap = buildContributionComponentMap(contributionSummary);

        List<String> ruleComponentCodes = extractComponentCodesFromMapping(matchedRule);

        if (ruleComponentCodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<ComponentBalanceDTO> result = new ArrayList<>();

        for (String code : ruleComponentCodes) {

            if (code == null || code.isBlank()) {
                continue;
            }

            String normalizedCode = code.trim().toUpperCase();

            BigDecimal amount = contributionMap.getOrDefault(
                    normalizedCode,
                    BigDecimal.ZERO);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            result.add(
                    ComponentBalanceDTO.builder()
                            .code(normalizedCode)
                            .name(normalizedCode)
                            .type(resolveComponentType(normalizedCode))
                            .amount(amount)
                            .build());
        }

        return result;
    }

    private String resolveComponentType(String code) {

        if (code == null) {
            return "UNKNOWN";
        }

        String value = code.trim().toUpperCase();

        if (value.startsWith("I") || value.endsWith("IC")) {
            return "INTEREST";
        }

        return "CONTRIBUTION";
    }

    private List<String> extractComponentCodesFromMapping(
            MatchedSubClaimRuleDto matchedRule) {

        List<String> codes = new ArrayList<>();

        if (matchedRule == null || matchedRule.getComponentMapping() == null) {
            return codes;
        }

        var mapping = matchedRule.getComponentMapping();

        boolean hasPf = "Y".equalsIgnoreCase(mapping.getHasPf());
        boolean hasPc = "Y".equalsIgnoreCase(mapping.getHasPc());

        if (hasPf) {
            if ("Y".equalsIgnoreCase(mapping.getHasMc()))
                codes.add("PF_MC");
            if ("Y".equalsIgnoreCase(mapping.getHasImc()))
                codes.add("PF_IMC");
            if ("Y".equalsIgnoreCase(mapping.getHasEc()))
                codes.add("PF_EC");
            if ("Y".equalsIgnoreCase(mapping.getHasIec()))
                codes.add("PF_IEC");
            if ("Y".equalsIgnoreCase(mapping.getHasGc()))
                codes.add("PF_GC");
            if ("Y".equalsIgnoreCase(mapping.getHasGic()))
                codes.add("PF_GIC");
            if ("Y".equalsIgnoreCase(mapping.getHasVc()))
                codes.add("PF_VC");
            if ("Y".equalsIgnoreCase(mapping.getHasVic()))
                codes.add("PF_VIC");
        }

        if (hasPc) {
            codes.add("PC");
        }

        return codes.stream()
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

    // private String buildVestingPreviewNote() {

    // }

}