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

    // ============================================================
    // DEPENDENCY INJECTIONS
    // ============================================================
    private final MemberContributionService memberContributionService;
    private final RuleService ruleService;
    private final PartialWithdrawalRuleService partialWithdrawalRuleService;
    private final LoanDetailService loanDetailService;
    private final ClaimTypeRuleMapRepository claimTypeRuleMapRepository;
    private final MemberService memberService;
    private final RentalDetailService rentalDetailService;
    private final ReserveAccountRepository reserveAccountRepository;
    private final PensionContributionComponentRepository pensionContributionComponentRepository;

    // ============================================================
    // MAIN PUBLIC METHODS
    // ============================================================

    /**
     * Main method to calculate benefit for a claim
     * 
     * @param request - ClaimInitialPreviewRequest containing claim details
     * @return ApiResponseDTO<ClaimCalculationResponseDTO> - Complete calculation response
     * 
     * Process Flow:
     * 1. Check if it's a partial withdrawal claim
     * 2. Get member details and contribution summary
     * 3. Apply rules to determine eligible components
     * 4. Calculate totals (PF, Pension, Interest)
     * 5. Check for loan and rental deductions
     * 6. Build and return response
     */
    @Override
    public ApiResponseDTO<ClaimCalculationResponseDTO> calculateBenefit(
            ClaimInitialPreviewRequest request) {
        
        // ===== STEP 1: Check if Partial Withdrawal =====
        if (isPartialWithdrawalRule(request.getClaimTypeId())) {
            return partialWithdrawalRuleService.calculatePartialWithdrawal(request);
        }

        // ===== STEP 2: Get Member Details =====
        MemberDetailResponseDto memberDetail = getMemberDetail(request.getNppfNumber());
        
        // ===== STEP 3: Get Contribution Summary =====
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(memberDetail, request.getCessationDate());

        // Validate contribution data
        if (contributionSummary == null) {
            throw ClaimException.notFound("No contribution snapshots found for the given member.");
        }
        if (contributionSummary.getComponentGroups() == null || contributionSummary.getComponentGroups().isEmpty()) {
            throw ClaimException.notFound("No contribution data found for member: " + request.getNppfNumber()
                    + " and identity number: " + request.getIdentityNumber());
        }

        // ===== STEP 4: Calculate Total Balance =====
        BigDecimal totalAmount = contributionSummary.getTotalBalance();

        // ===== STEP 5: Apply Rules to Determine Eligibility =====
        ApiResponseDTO<List<MatchedSubClaimRuleDto>> ruleResponse = ruleService.playWithRule(request);

        List<MatchedSubClaimRuleDto> matchedRules = ruleResponse == null || ruleResponse.getData() == null
                ? List.of()
                : ruleResponse.getData();

        if (matchedRules.isEmpty()) {
            return ApiResponseDTO.notFound("No matched rules found");
        }

        // ===== STEP 6: Get Claim Type Rule Mappings =====
        List<ClaimTypeRuleMap> claimRuleMaps = claimTypeRuleMapRepository
                .findByClaimTypeId(request.getClaimTypeId());
        if (claimRuleMaps == null || claimRuleMaps.isEmpty()) {
            return ApiResponseDTO
                    .notFound("No claim type rule mapping found for claim type id: "
                            + request.getClaimTypeId());
        }

        // ===== STEP 7: Check if Loan and Rental Apply =====
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

        // ===== STEP 8: Process Rules =====
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

        // Process each matched rule
        for (MatchedSubClaimRuleDto matchedRule : matchedRules) {
            if (matchedRule == null) {
                continue;
            }

            String ruleCode = safeUpper(matchedRule.getRuleCode());

            // ===== 8a. Handle VESTING Rule =====
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

            // ===== 8b. Handle LAPSED Rule =====
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

            // ===== 8c. Handle ELIGIBILITY Rule =====
            EligibilityResultDto eligibilityResult = handleEligibilityRule(
                    matchedRule,
                    request,
                    contributionSummary, expressionCalculations);

            if (eligibilityResult != null && eligibilityResult.getEligibleComponents() != null) {
                matchedRuleCodes.add(matchedRule.getSubClaimCode());
                eligibleComponents.addAll(eligibilityResult.getEligibleComponents());
            }
        }

        // ===== STEP 9: Calculate Totals by Component Type =====
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

            // Categorize components
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

        // ===== STEP 10: Check Loan and Rental Details =====
        isLoanApply = !loanDetailService.getLoanDetails(request.getNppfNumber()).getData().isEmpty();
        isRentalApply = !rentalDetailService.getRentalDetails(request.getNppfNumber()).getData().isEmpty();

        // ===== STEP 11: Calculate Service Years =====
        LocalDate joiningDate = toLocalDate(memberDetail.getDateOfServiceJoiningDate());
        BigDecimal serviceYears = contributionSummary == null
                ? BigDecimal.ZERO
                : calculateServiceYears(
                        joiningDate, request.getCessationDate());

        // ===== STEP 12: Build Eligibility Note =====
        String eligibilityNote = buildEligibilityPreviewNote(
                finalComponents,
                totalPfAmount,
                totalPensionAmount);

        // ===== STEP 13: Build and Return Response =====
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

    // ============================================================
    // SPECIAL CASE METHODS
    // ============================================================

    /**
     * Public method to get special case benefit
     * Handles different case types and returns appropriate response
     * 
     * @param nppfNumber - Member's NPPF number
     * @param isSpecialCase - "Y" for special case, "N" for normal
     * @return ApiResponseDTO<Object> - Special case response
     */
    @Override
    public ApiResponseDTO<Object> getSpecialCaseBenefit(String nppfNumber, String isSpecialCase) {

        // Calculate special case benefit
        ClaimCalculationResponseDTO calculationResponse = calculateSpecialCaseBenefit(nppfNumber, isSpecialCase);
        if (calculationResponse == null) {
            return ApiResponseDTO.success("No Detail Found with nppf number: " + nppfNumber);
        }
        SpecialCasePreviewResponse response = mapToSpecialCasePreviewResponse(calculationResponse);
        return ApiResponseDTO.success(response);
    }

    /**
     * Maps ClaimCalculationResponseDTO to SpecialCasePreviewResponse
     * 
     * @param calculationResponse - The calculation response
     * @return SpecialCasePreviewResponse - Mapped response
     */
    private SpecialCasePreviewResponse mapToSpecialCasePreviewResponse(
            ClaimCalculationResponseDTO calculationResponse) {

        // Map components from the response
        List<SpecialCasePreviewResponse.ComponentDto> componentDtos = Optional
                .ofNullable(calculationResponse.getComponents())
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(component -> {
                    String code = component.getCode();
                    return SpecialCasePreviewResponse.ComponentDto.builder()
                            .component(code)
                            .componentAmount(component.getAmount().toString())
                            .build();
                })
                .collect(Collectors.toList());

        return SpecialCasePreviewResponse.builder()
                .components(componentDtos)
                .showCalcutionButton(calculationResponse.isShowClculationButton() ? "Y" : "N")
                .build();
    }

    /**
     * Public method to get special case preview
     * 
     * @param nppfNumber - Member's NPPF number
     * @return SpecialCasePreviewResponse - Preview response
     */
    public SpecialCasePreviewResponse getSpecialCasePreview(String nppfNumber) {
        ClaimCalculationResponseDTO calculationResponse = calculateSpecialCaseBenefit(nppfNumber, null);
        return mapToSpecialCasePreviewResponse(calculationResponse);
    }

    /**
     * Calculate special case benefit where all components are eligible
     * 
     * @param nppfNumber - Member's NPPF number
     * @param isLegalRecovery - "Y" for legal recovery, "N" for normal
     * @return ClaimCalculationResponseDTO - Calculation response
     * 
     * Process Flow:
     * 1. Validate request
     * 2. Get member details
     * 3. Get contribution summary
     * 4. Build all components as eligible
     * 5. Apply filters if needed
     * 6. Calculate totals
     * 7. Build and return response
     */
    private ClaimCalculationResponseDTO calculateSpecialCaseBenefit(String nppfNumber, String isLegalRecovery) {

        // ===== STEP 1: Validate Request =====
        if (nppfNumber == null || nppfNumber.isBlank()) {
            throw ClaimException.badRequest("NPPF number is required");
        }

        // ===== STEP 2: Get Member Details =====
        MemberDetailResponseDto memberDetail = getMemberDetail(nppfNumber);

        // ===== STEP 3: Get Contribution Summary =====
        // All components are eligible in special case
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(memberDetail, null);

        if (contributionSummary == null || contributionSummary.getComponentGroups() == null) {
            throw ClaimException.notFound("No contribution data found for member: " + nppfNumber);
        }

        boolean showCalculationButton = true;

        // ===== STEP 4: Build All Components as Eligible =====
        List<ComponentBalanceDTO> allComponents = buildAllEligibleComponents(contributionSummary);
        
        // ===== STEP 5: Apply Filters for Normal Cases =====
        if (isLegalRecovery != null && isLegalRecovery.equals("N")) {
            List<ComponentBalanceDTO> filterComponents = filterTheComponents(nppfNumber, allComponents);
            if (filterComponents != null) {
                allComponents = filterComponents;
                showCalculationButton = false;
            }
        }

        // ===== STEP 6: Calculate Totals =====
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

            // Categorize components
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

        // ===== STEP 7: Calculate Total Amount =====
        BigDecimal totalAmount = (isLegalRecovery != null && isLegalRecovery.equals("N"))
                ? allComponents.stream()
                        .map(ComponentBalanceDTO::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                : contributionSummary.getTotalBalance();

        // ===== STEP 8: No Deductions in Special Case =====
        BigDecimal finalPayableAmount = totalAmount;

        // ===== STEP 9: Calculate Service Years =====
        LocalDate joiningDate = toLocalDate(memberDetail.getDateOfServiceJoiningDate());
        LocalDate endDate = LocalDate.now();
        BigDecimal serviceYears = calculateServiceYears(joiningDate, endDate);

        // ===== STEP 10: Build Response =====
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

    // ============================================================
    // FILTER METHODS
    // ============================================================

    /**
     * Filter components based on reserve account or pension contribution
     * 
     * @param nppfNumber - Member's NPPF number
     * @param allComponents - List of all components
     * @return List<ComponentBalanceDTO> - Filtered components
     */
    private List<ComponentBalanceDTO> filterTheComponents(String nppfNumber, List<ComponentBalanceDTO> allComponents) {
        List<ComponentBalanceDTO> result = allComponents
                .stream()
                .map(m -> {
                    // Check if reserve account exists for this component
                    ReserveAccount reserveAccount = reserveAccountRepository
                            .findByNppfNumberAndComponentCodeAndIsActive(nppfNumber, m.getCode(), "Y").orElse(null);
                    if (reserveAccount != null && m.getCode().equals(reserveAccount.getComponentCode())) {
                        return ComponentBalanceDTO
                                .builder()
                                .code(m.getCode())
                                .name(getComponentName(m.getName()))
                                .amount(reserveAccount.getTotalAmount())
                                .build();
                    }
                    
                    // Check if pension component exists
                    PensionContributionComponent pensionComponent = pensionContributionComponentRepository
                            .findActiveComponentsByNppfAndComponentCode(nppfNumber, m.getCode()).orElse(null);

                    if (pensionComponent != null && m.getCode().equals(pensionComponent.getComponentCode())) {
                        return ComponentBalanceDTO
                                .builder()
                                .code(m.getCode())
                                .name(getComponentName(m.getName()))
                                .amount(pensionComponent.getAmount())
                                .build();
                    }
                    return m;
                })
                .toList();
        return result;
    }

    // ============================================================
    // RULE HANDLING METHODS
    // ============================================================

    /**
     * Handle eligibility rule
     * 
     * @param matchedRule - Matched rule
     * @param request - Claim request
     * @param contributionSummary - Contribution summary
     * @param expressionCalculations - List to store expression calculations
     * @return EligibilityResultDto - Eligibility result
     */
    private EligibilityResultDto handleEligibilityRule(
            MatchedSubClaimRuleDto matchedRule,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary,
            List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

        // Try to get eligible components using formula
        List<ComponentBalanceDTO> eligible = getRuleAmountUsingFormulaIfAvailable(
                matchedRule,
                request,
                contributionSummary,
                "ELIGIBLE", expressionCalculations);

        // If no formula, get components directly from rule
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

    /**
     * Handle vesting rule
     * 
     * @param matchedRule - Matched rule
     * @return VestingResultDto - Vesting result
     */
    private VestingResultDto handleVestingRule(MatchedSubClaimRuleDto matchedRule) {
        return VestingResultDto.builder()
                .lumpSumEligible(matchedRule.getRefundTypeName() == null ? false : true)
                .refundTypeName(matchedRule.getRefundTypeName())
                .build();
    }

    /**
     * Handle lapsed rule
     * 
     * @param matchedRule - Matched rule
     * @param request - Claim request
     * @param contributionSummary - Contribution summary
     * @param expressionCalculations - List to store expression calculations
     * @return LapsedResultDto - Lapsed result
     */
    private LapsedResultDto handleLapsedRule(
            MatchedSubClaimRuleDto matchedRule,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary,
            List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

        // Try to get forfeited components using formula
        List<ComponentBalanceDTO> forfeited = getRuleAmountUsingFormulaIfAvailable(
                matchedRule,
                request,
                contributionSummary,
                "FORFEITED", expressionCalculations);

        // If no formula, get components directly from rule
        if (forfeited == null || forfeited.isEmpty()) {
            forfeited = getComponentsFromRule(
                    matchedRule,
                    contributionSummary);
        }

        // If still no components, return empty result
        if (forfeited == null || forfeited.isEmpty()) {
            return LapsedResultDto.builder()
                    .forfeited(false)
                    .forfeitedComponents(Collections.emptyList())
                    .forfeitedComponentCodes(Collections.emptyList())
                    .build();
        }

        // Extract component codes
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

    // ============================================================
    // COMPONENT BUILDING METHODS
    // ============================================================

    /**
     * Build all components as eligible (no rules applied)
     * This gets all components from the contribution summary
     * 
     * @param contributionSummary - Contribution summary
     * @return List<ComponentBalanceDTO> - All eligible components
     * 
     * IMPORTANT: Each component code represents both principal AND interest
     * For example: "PF_MC" is the code for both PF Member Contribution AND its interest
     * - Principal: PF_MC (CONTRIBUTION)
     * - Interest: PF_MC (INTEREST) - Same code, different type
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

            // Extract principal and interest amounts
            BigDecimal principal = component.getPrincipalAmount() != null
                    ? component.getPrincipalAmount()
                    : BigDecimal.ZERO;

            BigDecimal interest = component.getInterestAmount() != null
                    ? component.getInterestAmount()
                    : BigDecimal.ZERO;

            // ===== Add PRINCIPAL as CONTRIBUTION =====
            // The component code is used as-is (e.g., "PF_MC")
            if (principal.compareTo(BigDecimal.ZERO) > 0) {
                components.add(ComponentBalanceDTO.builder()
                        .code(code)
                        .name(getComponentName(code))
                        .type("CONTRIBUTION")
                        .amount(principal)
                        .build());
                System.out.println("  ✅ Added principal: " + code + " = " + principal + " (CONTRIBUTION)");
            }

            // ===== Add INTEREST as INTEREST =====
            // The SAME component code is used (e.g., "PF_MC" for interest too)
            // The type field distinguishes between principal and interest
            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                components.add(ComponentBalanceDTO.builder()
                        .code(code) // Same code as principal
                        .name(getComponentName(code))
                        .type("INTEREST")
                        .amount(interest)
                        .build());
                System.out.println("  ✅ Added interest: " + code + " = " + interest + " (INTEREST)");
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
     * Build contribution component map from summary
     * 
     * @param contributionSummary - Contribution summary
     * @return Map<String, BigDecimal> - Component code to amount map
     * 
     * IMPORTANT: Each component code maps to a specific amount
     * For example: "PF_MC" maps to the principal amount
     * "PF_IMC" maps to the interest amount
     */
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

            // Add interest amount - KEY FIX
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

    // ============================================================
    // COMPONENT RETRIEVAL METHODS
    // ============================================================

    /**
     * Get components from rule using formula if available
     * 
     * @param matchedRule - Matched rule
     * @param request - Claim request
     * @param contributionSummary - Contribution summary
     * @param calculationType - Type of calculation (ELIGIBLE/FORFEITED)
     * @param expressionCalculations - List to store expression calculations
     * @return List<ComponentBalanceDTO> - Components from rule
     */
    private List<ComponentBalanceDTO> getRuleAmountUsingFormulaIfAvailable(
            MatchedSubClaimRuleDto matchedRule,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary,
            String calculationType,
            List<ClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

        // Check if rule has expressions
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

        // Process each expression
        for (MatchedSubClaimRuleDto.ComponentExpression expressionDto : matchedRule.getComponentMapping()
                .getExpressions()) {
            if (expressionDto == null
                    || expressionDto.getExpression() == null
                    || expressionDto.getExpression().isBlank()) {
                continue;
            }

            // Resolve component codes from expression
            List<String> resolvedCodes = resolveExpressionComponentCodes(
                    expressionDto.getExpression(),
                    matchedRule.getComponentMapping(), componentAmountMap);

            System.out.println("Expression: " + expressionDto.getExpression());
            System.out.println("Resolved Codes: " + resolvedCodes);
            System.out.println("Available in map: " + resolvedCodes.stream()
                    .filter(componentAmountMap::containsKey)
                    .collect(Collectors.toList()));

            BigDecimal expressionAmount = BigDecimal.ZERO;

            // Process each resolved component
            for (String componentCode : resolvedCodes) {
                BigDecimal amount = componentAmountMap.getOrDefault(componentCode, BigDecimal.ZERO);

                // Skip if amount is zero
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("  Skipping " + componentCode + " (amount: " + amount + ")");
                    continue;
                }

                expressionAmount = expressionAmount.add(amount);

                // Add to results
                results.add(ComponentBalanceDTO.builder()
                        .subRuleCode(matchedRule.getSubClaimCode())
                        .code(componentCode)
                        .name(getComponentName(componentCode))
                        .type(calculationType)
                        .amount(amount)
                        .build());
            }

            // Store expression calculation
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

    /**
     * Resolve component codes from expression
     * 
     * @param expression - Expression string
     * @param mapping - Component mapping
     * @param componentAmountMap - Component amount map
     * @return List<String> - Resolved component codes
     */
    private List<String> resolveExpressionComponentCodes(
            String expression,
            MatchedSubClaimRuleDto.ComponentMapping mapping,
            Map<String, BigDecimal> componentAmountMap) {

        if (expression == null || expression.isBlank()
                || componentAmountMap == null
                || componentAmountMap.isEmpty()) {
            return Collections.emptyList();
        }

        // Split expression by + and - operators
        String[] tokens = expression
                .replace(" ", "")
                .toUpperCase()
                .split("[+\\-]");

        // Filter tokens that exist in component map
        return Arrays.stream(tokens)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .filter(componentAmountMap::containsKey)
                .distinct()
                .toList();
    }

    /**
     * Get components directly from rule (without formula)
     * 
     * @param matchedRule - Matched rule
     * @param contributionSummary - Contribution summary
     * @return List<ComponentBalanceDTO> - Components from rule
     */
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

        // Process each expression
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

            // Add each resolved component to result
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
                                .name(getComponentName(normalizedCode))
                                .type(resolveComponentType(normalizedCode))
                                .amount(amount)
                                .build());
            }
        }

        return result;
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    /**
     * Resolve component type (CONTRIBUTION or INTEREST)
     * 
     * @param code - Component code
     * @return String - Component type
     */
    private String resolveComponentType(String code) {
        if (code == null) {
            return null;
        }

        String value = code.trim().toUpperCase();

        // Check if it's an interest component
        if (value.startsWith("I") || value.endsWith("IC")) {
            return "INTEREST";
        }

        return "CONTRIBUTION";
    }

    /**
     * Get component name from code
     * 
     * @param code - Component code
     * @return String - Component name
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

    /**
     * Get member details by NPPF number
     * 
     * @param nppfNumber - Member's NPPF number
     * @return MemberDetailResponseDto - Member details
     */
    private MemberDetailResponseDto getMemberDetail(String nppfNumber) {
        ApiResponseDTO<MemberDetailResponseDto> response = memberService.getMemberDetails(nppfNumber);

        if (response == null || response.getData() == null) {
            throw ClaimException.notFound(
                    "Member detail not found for nppfNumber: " + nppfNumber);
        }

        return response.getData();
    }

    /**
     * Check if claim type is partial withdrawal
     * 
     * @param claimTypeId - Claim type ID
     * @return boolean - True if partial withdrawal
     */
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

    /**
     * Convert java.sql.Date to LocalDate
     * 
     * @param date - SQL Date
     * @return LocalDate - Converted date
     */
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

    /**
     * Calculate service years between joining date and end date
     * 
     * @param joiningDate - Member's joining date
     * @param endDate - End date
     * @return BigDecimal - Service years
     */
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

    /**
     * Build eligibility preview note
     * 
     * @param finalComponents - Final eligible components
     * @param totalPfAmount - Total PF amount
     * @param totalPensionAmount - Total Pension amount
     * @return String - Eligibility note
     */
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

    /**
     * Print debug information for matched rule
     * 
     * @param matchedRule - Matched rule
     */
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

    /**
     * Check if rule is a lapsed rule
     * 
     * @param ruleCode - Rule code
     * @return boolean - True if lapsed rule
     */
    private boolean isLapsedRule(String ruleCode) {
        if (ruleCode == null) {
            return false;
        }
        String upperCode = ruleCode.toUpperCase();
        return upperCode.contains("LAPSED")
                || upperCode.contains("NORMAL_LAPSED")
                || upperCode.contains("TERMINATION_LAPSED");
    }

    /**
     * Check if rule is a vesting rule
     * 
     * @param ruleCode - Rule code
     * @return boolean - True if vesting rule
     */
    private boolean isVestingRule(String ruleCode) {
        if (ruleCode == null) {
            return false;
        }
        return ruleCode.toUpperCase().contains("VESTING");
    }

    /**
     * Convert string to uppercase safely
     * 
     * @param value - String value
     * @return String - Uppercase string
     */
    private String safeUpper(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}