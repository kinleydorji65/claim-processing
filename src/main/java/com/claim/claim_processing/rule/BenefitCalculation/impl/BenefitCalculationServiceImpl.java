package com.claim.claim_processing.rule.BenefitCalculation.impl;

import com.claim.claim_processing.integration.pension.entity.PensionContributionComponent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.loanAdjustment.service.LoanDetailService;
import com.claim.claim_processing.integration.member.service.MemberService;
import com.claim.claim_processing.integration.pension.repository.PensionContributionComponentRepository;
import com.claim.claim_processing.integration.rentalAdjustment.service.RentalDetailService;
import com.claim.claim_processing.rule.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.claim.DTO.response.EligibilityResultDto;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedResultDto;
import com.claim.claim_processing.rule.claim.DTO.response.SpecialCasePreviewResponse;
import com.claim.claim_processing.rule.claim.DTO.response.VestingResultDto;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleProcessing.dto.MatchedSubClaimRuleDto;
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
    private final MemberService memberService;
    private final RentalDetailService rentalDetailService;

    private final ReserveAccountRepository reserveAccountRepository;
    private final PensionContributionComponentRepository pensionContributionComponentRepository;


    @Override
    public ApiResponseDTO<ClaimCalculationResponseDTO> calculateBenefit(
            ClaimInitialPreviewRequest request) {
        if (isPartialWithdrawalRule(request.getClaimTypeId())) {
            return partialWithdrawalRuleService.calculatePartialWithdrawal(request);
        }
        MemberDetailResponseDto memberDetail = getMemberDetail(request.getNppfNumber());
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(memberDetail, request.getCessationDate());

        if (contributionSummary == null) {
            throw ClaimException.notFound("No contribution snapshots found for the given member.");
        }
        if (contributionSummary.getComponentGroups() == null || contributionSummary.getComponentGroups().isEmpty()) {
            throw ClaimException.notFound("No contribution data found for member: " + request.getNppfNumber()
                    + " and identity number: " + request.getIdentityNumber());
        }
        BigDecimal totalAmount = contributionSummary.getTotalBalance();
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
        List<String> matchedRuleCodes = new ArrayList<>();
        VestingResultDto vestingResult = null;
        for (MatchedSubClaimRuleDto matchedRule : matchedRules) {

            if (matchedRule == null) {
                continue;
            }
            printMatchedRuleDebug(matchedRule);

            String ruleCode = safeUpper(matchedRule.getRuleCode());

            if (isVestingRule(ruleCode)) {

                vestingResult = handleVestingRule(matchedRule);

                if (vestingResult != null && vestingResult.isLumpSumEligible()) {

                    if (vestingResult.getRefundTypeName() != null
                            && !vestingResult.getRefundTypeName().isBlank()) {
                        vestingNote = "Till Date, Your total Contribution Months is " + totalMonths
                                + ". Recommended benefit type is " + vestingResult.getRefundTypeName()
                                + (vestingResult.isLumpSumEligible() ? " and it is Eligible."
                                        : " and it is Not Eligible.");
                        recommendedRefundTypes.add(vestingResult.getRefundTypeName());
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
                matchedRuleCodes.add(matchedRule.getSubClaimCode());
                eligibleComponents.addAll(eligibilityResult.getEligibleComponents());
            }
        }
        List<ComponentBalanceDTO> finalComponents = eligibleComponents.stream()
                .filter(Objects::nonNull)
                .toList();

        BigDecimal totalPfAmount = BigDecimal.ZERO;
        BigDecimal totalPensionAmount = BigDecimal.ZERO;
        BigDecimal totalPfInterestAmount = BigDecimal.ZERO;
        BigDecimal totalPensionInterestAmount = BigDecimal.ZERO;

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
                    totalPfAmount = totalPfAmount.add(amount);
                    break;

                case "PF_IMC":
                case "PF_IEC":
                    totalPfInterestAmount = totalPfInterestAmount.add(amount);
                    break;

                case "P_MC":
                case "P_EC":
                    totalPensionAmount = totalPensionAmount.add(amount);
                    break;

                case "P_IMC":
                case "P_IEC":
                    totalPensionInterestAmount = totalPensionInterestAmount.add(amount);
                    break;

                default:
                    break;
            }
        }
        isLoanApply = !loanDetailService.getLoanDetails(request.getNppfNumber()).getData().isEmpty();
        isRentalApply = !rentalDetailService.getRentalDetails(request.getNppfNumber()).getData().isEmpty();

        LocalDate joiningDate = toLocalDate(memberDetail.getDateOfServiceJoiningDate());

        BigDecimal serviceYears = contributionSummary == null
                ? BigDecimal.ZERO
                : calculateServiceYears(
                        joiningDate, request.getCessationDate());

        String eligibilityNote = buildEligibilityPreviewNote(
                finalComponents,
                totalPfAmount,
                totalPensionAmount);

        ClaimCalculationResponseDTO response = ClaimCalculationResponseDTO.builder()
                .nppfNumber(contributionSummary != null ? contributionSummary.getNppfNumber() : null)
                .contributionStartDate(
                        contributionSummary != null
                                ? memberDetail.getPfJoiningDate()
                                : null)
                .loanNote(isLoanApply ? "You have a loan details to deduct." : null)
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
                .totalAmount(totalAmount)
                .components(finalComponents)
                .expressionCalculations(expressionCalculations)
                .loanCheck(isLoanApply)
                .rentalCheck(isRentalApply)
                .rentalNote(isRentalApply
                        ? "A rental deduction has been identified. The deduction will be reviewed and confirmed by the approver during the approval process."
                        : null)
                .totalPfAmount(totalPfAmount)
                .totalPensionAmount(totalPensionAmount)
                .totalPfInterestAmount(totalPfInterestAmount)
                .totalPensionInterestAmount(totalPensionInterestAmount)
                .pfIsEligible(totalPfAmount.compareTo(BigDecimal.ZERO) > 0
                        ? EligibilityEnum.ELIGIBLE
                        : EligibilityEnum.NOT_ELIGIBLE)
                .pensionIsEligible(vestingResult != null && vestingResult.getRefundTypeName() != null
                        && vestingResult.getRefundTypeName().equals("Pension")
                                ? EligibilityEnum.ELIGIBLE
                                : EligibilityEnum.NOT_ELIGIBLE)
                .eligibilityNote(eligibilityNote)
                .vestingNote(vestingNote)
                .recommendedBenefitType(String.join(" ", recommendedRefundTypes))
                .finalPayableAmount(null)
                .forfeitedComponents(forfeitedComponents)
                .build();

        return ApiResponseDTO.success(response);
    }

    private LocalDate toLocalDate(Date date) {

        if (date == null) {
            return null;
        }

        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
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
            LocalDate joiningDate,
            LocalDate endDate) {

        if (joiningDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }

        long months = ChronoUnit.MONTHS.between(joiningDate, endDate);

        return BigDecimal.valueOf(months)
                .divide(BigDecimal.valueOf(12), 1, RoundingMode.HALF_UP);
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
    }

    private boolean isLapsedRule(String ruleCode) {
        if (ruleCode == null) {
            return false;
        }
        String upperCode = ruleCode.toUpperCase();
        return upperCode.contains("LAPSED")
                || upperCode.contains("NORMAL_LAPSED")
                || upperCode.contains("TERMINATION_LAPSED");
    }

    private boolean isVestingRule(String ruleCode) {
        if (ruleCode == null) {
            return false;
        }
        return ruleCode.toUpperCase().contains("VESTING");
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
                .lumpSumEligible(matchedRule.getRefundTypeName() == null ? false : true)
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

        // DEBUG: Print available components
        System.out.println("========== AVAILABLE COMPONENTS ==========");
        for (Map.Entry<String, BigDecimal> entry : componentAmountMap.entrySet()) {
            System.out.println("  " + entry.getKey() + " = " + entry.getValue());
        }

        List<ComponentBalanceDTO> results = new ArrayList<>();

        for (MatchedSubClaimRuleDto.ComponentExpression expressionDto : matchedRule.getComponentMapping()
                .getExpressions()) {
            if (expressionDto == null
                    || expressionDto.getExpression() == null
                    || expressionDto.getExpression().isBlank()) {
                continue;
            }

            List<String> resolvedCodes = resolveExpressionComponentCodes(
                    expressionDto.getExpression(),
                    matchedRule.getComponentMapping(), componentAmountMap);

            System.out.println("Expression: " + expressionDto.getExpression());
            System.out.println("Resolved Codes: " + resolvedCodes);
            System.out.println("Available in map: " + resolvedCodes.stream()
                    .filter(componentAmountMap::containsKey)
                    .collect(Collectors.toList()));

            BigDecimal expressionAmount = BigDecimal.ZERO;

            for (String componentCode : resolvedCodes) {
                // Check if component exists in map and has amount > 0
                BigDecimal amount = componentAmountMap.getOrDefault(componentCode, BigDecimal.ZERO);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("  Skipping " + componentCode + " (amount: " + amount + ")");
                    continue;
                }

                expressionAmount = expressionAmount.add(amount);

                results.add(ComponentBalanceDTO.builder()
                        .subRuleCode(matchedRule.getSubClaimCode())
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
            MatchedSubClaimRuleDto.ComponentMapping mapping,
            Map<String, BigDecimal> componentAmountMap) {

        if (expression == null || expression.isBlank()
                || componentAmountMap == null
                || componentAmountMap.isEmpty()) {
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

    private MemberDetailResponseDto getMemberDetail(String nppfNumber) {

        ApiResponseDTO<MemberDetailResponseDto> response = memberService.getMemberDetails(nppfNumber);

        if (response == null || response.getData() == null) {
            throw ClaimException.notFound(
                    "Member detail not found for nppfNumber: " + nppfNumber);
        }

        return response.getData();
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

        // Get expressions from the rule
        List<MatchedSubClaimRuleDto.ComponentExpression> expressions = matchedRule.getComponentMapping()
                .getExpressions();

        if (expressions == null || expressions.isEmpty()) {
            return Collections.emptyList();
        }

        List<ComponentBalanceDTO> result = new ArrayList<>();

        for (MatchedSubClaimRuleDto.ComponentExpression expressionDto : expressions) {

            if (expressionDto == null
                    || expressionDto.getExpression() == null
                    || expressionDto.getExpression().isBlank()) {
                continue;
            }

            String expression = expressionDto.getExpression();

            // Resolve component codes from the expression
            List<String> resolvedCodes = resolveExpressionComponentCodes(
                    expression,
                    matchedRule.getComponentMapping(),
                    contributionMap);

            if (resolvedCodes.isEmpty()) {
                continue;
            }

            for (String code : resolvedCodes) {

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
        }

        return result;
    }

    private String resolveComponentType(String code) {

        if (code == null) {
            return null;
        }

        String value = code.trim().toUpperCase();

        if (value.startsWith("I") || value.endsWith("IC")) {
            return "INTEREST";
        }

        return "CONTRIBUTION";
    }

    private Map<String, BigDecimal> buildContributionComponentMap(
            MemberContributionSummary contributionSummary) {

        Map<String, BigDecimal> map = new HashMap<>();

        if (contributionSummary == null
                || contributionSummary.getComponentGroups() == null) {
            System.out.println("No component groups found in contribution summary");
            return map;
        }

        System.out.println("========== BUILDING COMPONENT MAP ==========");
        System.out.println("Total component groups: " + contributionSummary.getComponentGroups().size());

        for (MemberContributionSummary.ComponentGroup component : contributionSummary.getComponentGroups()) {
            if (component == null || component.getComponentCode() == null) {
                continue;
            }

            String code = component.getComponentCode().trim().toUpperCase();

            System.out.println("Processing: " + code);
            System.out.println("  Principal: " + component.getPrincipalAmount());
            System.out.println("  Interest: " + component.getInterestAmount());
            System.out.println("  Total: " + component.getTotalAmount());

            // Add principal amount
            if (component.getPrincipalAmount() != null &&
                    component.getPrincipalAmount().compareTo(BigDecimal.ZERO) > 0) {
                map.put(code, component.getPrincipalAmount());
                System.out.println("  ✅ Added principal: " + code + " = " + component.getPrincipalAmount());
            }

            // Add interest amount - THIS IS THE KEY FIX
            if (component.getInterestAmount() != null &&
                    component.getInterestAmount().compareTo(BigDecimal.ZERO) > 0) {
                // The component code already IS the interest code (PF_IEC, PF_IMC, etc.)
                // So we add it with the same code
                map.put(code, component.getInterestAmount());
                System.out.println("  ✅ Added interest: " + code + " = " + component.getInterestAmount());
            }
        }

        System.out.println("========== FINAL COMPONENT MAP ==========");
        for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
            System.out.println("  " + entry.getKey() + " = " + entry.getValue());
        }

        return map;
    }

    /**
     * Public method to get special case benefit
     * Handles different case types and returns appropriate response
     */
    @Override
    public ApiResponseDTO<Object> getSpecialCaseBenefit(String nppfNumber, String isSpecialCase) {

        // Handle NORMAL_CLAIM_FORFEITED and SPECIAL_NORMAL_CLAIM
        ClaimCalculationResponseDTO calculationResponse = calculateSpecialCaseBenefit(nppfNumber, isSpecialCase);
        if (calculationResponse == null) {
            return ApiResponseDTO.success("No Detail Found with nppf number: " + nppfNumber);
        }
        SpecialCasePreviewResponse response = mapToSpecialCasePreviewResponse(calculationResponse);
        return ApiResponseDTO.success(response);
    }

    /**
     * Maps ClaimCalculationResponseDTO to SpecialCasePreviewResponse
     */
    private SpecialCasePreviewResponse mapToSpecialCasePreviewResponse(
            ClaimCalculationResponseDTO calculationResponse) {
               
        // Map components from the response - FIXED
        List<SpecialCasePreviewResponse.ComponentDto> componentDtos = Optional
                .ofNullable(calculationResponse.getComponents())
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(component -> {
                    // Get the component code
                    System.out.println("component code checking: " + component.getCode());
                    String code = component.getCode();

                    return SpecialCasePreviewResponse.ComponentDto.builder()
                            .component(code)
                            .componentAmount(component.getAmount().toString())
                            .build();
                })
                .collect(Collectors.toList());

        // Return the complete response
        return SpecialCasePreviewResponse.builder()
                .components(componentDtos)
                .showCalcutionButton(calculationResponse.isShowClculationButton() ? "Y" : "N")
                .build();
    }

    /**
     * Public method to get special case preview
     */
    public SpecialCasePreviewResponse getSpecialCasePreview(String nppfNumber) {
        ClaimCalculationResponseDTO calculationResponse = calculateSpecialCaseBenefit(nppfNumber, null);
        return mapToSpecialCasePreviewResponse(calculationResponse);
    }

    /**
     * Calculate special case benefit where all components are eligible
     */
    private ClaimCalculationResponseDTO calculateSpecialCaseBenefit(String nppfNumber, String isLegalRecovery) {

        // 1. Validate request
        if (nppfNumber == null || nppfNumber.isBlank()) {
            throw ClaimException.badRequest("NPPF number is required");
        }

        // 2. Get member details
        MemberDetailResponseDto memberDetail = getMemberDetail(nppfNumber);

        // 3. Get contribution summary (ALL components are eligible in special case)
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(memberDetail, null);

        if (contributionSummary == null || contributionSummary.getComponentGroups() == null) {
            throw ClaimException.notFound("No contribution data found for member: " + nppfNumber);
        }

        boolean showCalculationButton = true;
        // 4. Build all components as eligible (no rules applied)
        List<ComponentBalanceDTO> allComponents = buildAllEligibleComponents(contributionSummary);
        if(isLegalRecovery.equals("N")){
            List<ComponentBalanceDTO> filterComponents = filterTheComponents(nppfNumber, allComponents);

            if (filterComponents != null) {
                allComponents = filterComponents;
                showCalculationButton = false;
            }
        }

        // 5. Calculate totals using the same logic as your main service
        BigDecimal totalPfAmount = BigDecimal.ZERO;
        BigDecimal totalPensionAmount = BigDecimal.ZERO;
        BigDecimal totalPfInterestAmount = BigDecimal.ZERO;
        BigDecimal totalPensionInterestAmount = BigDecimal.ZERO;

        for (ComponentBalanceDTO component : allComponents) {
            if (component == null || component.getCode() == null) {
                continue;
            }

            String code = component.getCode().trim().toUpperCase();
            BigDecimal amount = component.getAmount() == null ? BigDecimal.ZERO : component.getAmount();

            // Use the same mapping logic as your main service
            switch (code) {
                case "PF_MC":
                case "PF_EC":
                    totalPfAmount = totalPfAmount.add(amount);
                    break;

                case "PF_IMC":
                case "PF_IEC":
                    totalPfInterestAmount = totalPfInterestAmount.add(amount);
                    break;

                case "P_MC":
                case "P_EC":
                case "PC_MC":
                case "PC_EC":
                    totalPensionAmount = totalPensionAmount.add(amount);
                    break;

                case "P_IMC":
                case "P_IEC":
                case "PC_IMC":
                case "PC_IEC":
                    totalPensionInterestAmount = totalPensionInterestAmount.add(amount);
                    break;

                default:
                    break;
            }
        }

        // 6. Calculate total amount
        BigDecimal totalAmount = contributionSummary.getTotalBalance() != null
                ? contributionSummary.getTotalBalance()
                : allComponents.stream()
                        .map(ComponentBalanceDTO::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 7. No deductions in special case (no loan, no rental)
        BigDecimal finalPayableAmount = totalAmount;

        // 8. Calculate service years
        LocalDate joiningDate = toLocalDate(memberDetail.getDateOfServiceJoiningDate());
        LocalDate endDate = LocalDate.now();

        BigDecimal serviceYears = calculateServiceYears(joiningDate, endDate);

        // 9. Build response
        ClaimCalculationResponseDTO response = ClaimCalculationResponseDTO.builder()
                .nppfNumber(contributionSummary.getNppfNumber())
                .showClculationButton(showCalculationButton)
                .contributionStartDate(memberDetail.getPfJoiningDate())
                .contributionEndDate(contributionSummary.getContributionEndDate())
                .totalContributionMonths(contributionSummary.getTotalContributionMonths())
                .totalNonContributionMonths(contributionSummary.getTotalNonContributionMonths())
                .noOfYearInService(serviceYears)
                .totalAmount(totalAmount)
                .components(allComponents)
                .loanCheck(false)
                .rentalCheck(false)
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
                .finalPayableAmount(finalPayableAmount)
                .forfeitedComponents(Collections.emptyList())
                .vestingNote("Special Case: All components are eligible for lump sum withdrawal")
                .recommendedBenefitType("Lump Sum")
                .eligibilityNote("Special Case: All contributions are eligible")
                .expressionCalculations(Collections.emptyList())
                .build();

        return response;
    }

    private List<ComponentBalanceDTO> filterTheComponents(String nppfNumber, List<ComponentBalanceDTO> allComponents) {
        List<ComponentBalanceDTO> result = allComponents
            .stream()
            .map(m -> {
                ReserveAccount reserveAccount = reserveAccountRepository.findByNppfNumberAndComponentCodeAndIsActive(nppfNumber, m.getCode(), "Y").orElse(null);
                if (reserveAccount != null && m.getCode().equals(reserveAccount.getComponentCode())) {
                    return ComponentBalanceDTO
                        .builder()
                        .code(m.getCode())
                        .name(m.getName())
                        .amount(reserveAccount.getTotalAmount())
                        .build();    
                }
                PensionContributionComponent pensionComponent = pensionContributionComponentRepository.findActiveComponentsByNppfAndComponentCode(nppfNumber, m.getCode()).orElse(null);
                

                if (pensionComponent != null && m.getCode().equals(pensionComponent.getComponentCode())) {
                    return ComponentBalanceDTO
                        .builder()
                        .code(m.getCode())
                        .name(m.getName())
                        .amount(pensionComponent.getAmount())
                        .build();
                }
                return m;
            })
            .toList();
            return result;
    }
    /**
 * Build all components as eligible (no rules applied)
 * This gets all components from the contribution summary
 */
private List<ComponentBalanceDTO> buildAllEligibleComponents(MemberContributionSummary contributionSummary) {
    List<ComponentBalanceDTO> components = new ArrayList<>();

    if (contributionSummary == null || contributionSummary.getComponentGroups() == null) {
        return components;
    }

    System.out.println("========== BUILDING ALL ELIGIBLE COMPONENTS ==========");
    System.out.println("Total component groups: " + contributionSummary.getComponentGroups().size());

    for (MemberContributionSummary.ComponentGroup component : contributionSummary.getComponentGroups()) {
        if (component == null || component.getComponentCode() == null) {
            continue;
        }

        String code = component.getComponentCode().trim().toUpperCase();
        System.out.println("Processing: " + code);

        BigDecimal principal = component.getPrincipalAmount() != null
                ? component.getPrincipalAmount()
                : BigDecimal.ZERO;

        BigDecimal interest = component.getInterestAmount() != null
                ? component.getInterestAmount()
                : BigDecimal.ZERO;

        // ✅ If there's a principal amount, add it as CONTRIBUTION
        if (principal.compareTo(BigDecimal.ZERO) > 0) {
            components.add(ComponentBalanceDTO.builder()
                    .code(code)
                    .name(getComponentName(code))
                    .type("CONTRIBUTION")
                    .amount(principal)
                    .build());
            System.out.println("  ✅ Added principal: " + code + " = " + principal);
        }

        // ✅ If there's an interest amount, add it as INTEREST (use the SAME code)
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            components.add(ComponentBalanceDTO.builder()
                    .code(code)  // ✅ Use the same code - it's already the interest code
                    .name(getComponentName(code))
                    .type("INTEREST")
                    .amount(interest)
                    .build());
            System.out.println("  ✅ Added interest: " + code + " = " + interest);
        }
    }

    System.out.println("========== FINAL COMPONENTS ==========");
    System.out.println("Total components built: " + components.size());
    for (ComponentBalanceDTO comp : components) {
        System.out.println("  " + comp.getCode() + " = " + comp.getAmount() + " (" + comp.getType() + ")");
    }

    return components;
}

    /**
     * Get component name
     */
    private String getComponentName(String code) {
        if (code == null)
            return null;

        Map<String, String> nameMap = new HashMap<>();

        // Principal components
        nameMap.put("PF_MC", "Employer's Contribution to PF");
        nameMap.put("PF_EC", "Member's Contribution to PF");
        nameMap.put("P_EC", "Employer's Pension Contribution");
        nameMap.put("GC", "Government Contribution to PF");
        nameMap.put("VC", "Voluntary Contribution to PF");

        // Interest components - FIXED with correct codes
        nameMap.put("PF_IMC", "Interest on Employer's PF Contribution");
        nameMap.put("PF_IEC", "Interest on Member's PF Contribution");
        nameMap.put("P_IEC", "Interest on Employer's Pension");
        nameMap.put("IGC", "Interest on Government PF Contribution");
        nameMap.put("IVC", "Interest on Voluntary PF Contribution");

        // Alternative/legacy codes
        nameMap.put("PF_GC", "Government Contribution to PF");
        nameMap.put("PF_IGC", "Interest on Government PF Contribution");
        nameMap.put("P_MC", "Member's Pension Contribution");
        nameMap.put("P_IMC", "Interest on Member's Pension");
        nameMap.put("PC_MC", "Member's Pension Contribution");
        nameMap.put("PC_EC", "Employer's Pension Contribution");
        nameMap.put("PC_IMC", "Interest on Member's Pension");
        nameMap.put("PC_IEC", "Interest on Employer's Pension");

        return nameMap.getOrDefault(code, code);
    }
}