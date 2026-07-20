package com.claim.claim_processing.rule.BenefitCalculation.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.adjustmentMaster.LoanTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.entities.common.RentalMaster;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.repository.adjustmentMaster.LoanTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.common.repository.common.RentalMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.loanAdjustment.dto.LoanAdjustmentResultDto;
import com.claim.claim_processing.integration.loanAdjustment.dto.LoanDetailResponseDto;
import com.claim.claim_processing.integration.loanAdjustment.dto.RentalAdjustmentResultDto;
import com.claim.claim_processing.integration.loanAdjustment.service.LoanDetailService;
import com.claim.claim_processing.integration.member.service.MemberService;
import com.claim.claim_processing.integration.rentalAdjustment.dto.RentalDetailResponseDto;
import com.claim.claim_processing.integration.rentalAdjustment.service.RentalDetailService;
import com.claim.claim_processing.rule.BenefitCalculation.VerifierBenefitCalculationService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.DTO.response.LoanAdjustmentDetailDto;
import com.claim.claim_processing.rule.claim.DTO.response.RentalAdjustmentDetailDto;
import com.claim.claim_processing.rule.claim.DTO.response.VerifierClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.VerifierClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.claim.DTO.response.VerifierEligibilityResultDto;
import com.claim.claim_processing.rule.claim.DTO.response.VerifierLapsedResultDto;
import com.claim.claim_processing.rule.claim.DTO.response.VestingResultDto;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleProcessing.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.LoanDeductionMapping;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.RentalDeductionMapping;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.LoanDeductionMappingRepository;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.RentalDeductionMappingRepository;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;
import com.claim.claim_processing.rule.ruleProcessing.service.VerifierPartialWithdrawalRuleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VerifierBenefitCalculationServiceImpl implements VerifierBenefitCalculationService {

    private final MemberContributionService memberContributionService;
    private final RuleService ruleService;
    private final VerifierPartialWithdrawalRuleService partialWithdrawalRuleService;
    private final LoanDetailService loanDetailService;
    private final ClaimTypeRuleMapRepository claimTypeRuleMapRepository;
    private final LoanDeductionMappingRepository loanDeductionMappingRepository;
    private final MemberService memberService;
    private final RentalDetailService rentalDetailService;
    private final RentalDeductionMappingRepository rentalDeductionMappingRepository;
    private final RentalMasterRepository rentalMasterRepository;
    private final LoanTypeRepository loanTypeRepository;

    @Override
    public ApiResponseDTO<VerifierClaimCalculationResponseDTO> calculateBenefit(
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
        String recommendedRefundType = null;
        List<String> forfeitedComponentCodes = new ArrayList<>();

        Integer totalMonths = contributionSummary == null
                ? null
                : contributionSummary.getTotalContributionMonths();
        List<VerifierClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations = new ArrayList<>();
        List<String> matchedRuleCodes = new ArrayList<>();
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
                        vestingNote = "Till Date, Your total Contribution Months is " + totalMonths
                                + ". Recommended benefit type is " + vestingResult.getRefundTypeName()
                                + (vestingResult.isLumpSumEligible() ? " and it is Eligible."
                                        : " and it is Not Eligible.");
                        recommendedRefundType = "vestingResult.getRefundTypeName()";
                    }

                }

                continue;
            }

            if (isLapsedRule(ruleCode)) {

                VerifierLapsedResultDto lapsedResult = handleLapsedRule(
                        matchedRule,
                        request,
                        contributionSummary, expressionCalculations);

                if (lapsedResult != null && lapsedResult.isForfeited()) {
                    forfeitedComponents.addAll(lapsedResult.getForfeitedComponents());
                    forfeitedComponentCodes.addAll(lapsedResult.getForfeitedComponentCodes());
                }

                continue;
            }

            VerifierEligibilityResultDto eligibilityResult = handleEligibilityRule(
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
        RentalAdjustmentResultDto rentalAdjustmentResult = null;
        BigDecimal finalPayableAmount = grossPayableAmount;
        List<Long> ruleTypeIds = claimRuleMaps.stream()
                .filter(Objects::nonNull)
                .filter(map -> map.getRuleType() != null)
                .map(map -> map.getRuleType().getId())
                .toList();

        // 1. LOAN DEDUCTION (FIRST)
        if (isLoanApply) {
            loanAdjustmentResult = deductLoanByPriority(
                    request.getNppfNumber(),
                    totalPfAmount, totalPfInterestAmount, finalPayableAmount, recommendedRefundType, ruleTypeIds);

            if (loanAdjustmentResult != null) {
                BigDecimal loanDeductionAmount = loanAdjustmentResult.getTotalAdjustedAmount();

                // Deduct from PF first
                BigDecimal deductedFromPf = totalPfAmount.min(loanDeductionAmount);
                totalPfAmount = totalPfAmount.subtract(deductedFromPf);

                BigDecimal remainingDeduction = loanDeductionAmount.subtract(deductedFromPf);
                if (remainingDeduction.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal deductedFromInterest = totalPfInterestAmount.min(remainingDeduction);
                    totalPfInterestAmount = totalPfInterestAmount.subtract(deductedFromInterest);
                }

                finalPayableAmount = totalPfAmount.add(totalPfInterestAmount);
            }
        }

        // 2. RENTAL DEDUCTION (SECOND - AFTER LOAN)
        if (request.getIsVerifier().equals("N")) {
            if (isRentalApply) {
                rentalAdjustmentResult = deductRental(
                        request.getNppfNumber(),
                        totalPfAmount,
                        totalPfInterestAmount,
                        ruleTypeIds);

                if (rentalAdjustmentResult != null) {
                    BigDecimal rentalDeductionAmount = rentalAdjustmentResult.getTotalAdjustedAmount();

                    // Deduct from PF first
                    BigDecimal deductedFromPf = totalPfAmount.min(rentalDeductionAmount);
                    totalPfAmount = totalPfAmount.subtract(deductedFromPf);

                    BigDecimal remainingDeduction = rentalDeductionAmount.subtract(deductedFromPf);
                    if (remainingDeduction.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal deductedFromInterest = totalPfInterestAmount.min(remainingDeduction);
                        totalPfInterestAmount = totalPfInterestAmount.subtract(deductedFromInterest);
                    }

                    finalPayableAmount = totalPfAmount.add(totalPfInterestAmount);
                }
            }
        }

        LocalDate joiningDate = toLocalDate(memberDetail.getDateOfServiceJoiningDate());

        BigDecimal serviceYears = contributionSummary == null
                ? BigDecimal.ZERO
                : calculateServiceYears(
                        joiningDate,
                        request.getCessationDate() != null
                                ? request.getCessationDate()
                                : memberDetail.getPfJoiningDate());

        String eligibilityNote = buildEligibilityPreviewNote(
                finalComponents,
                totalPfAmount,
                totalPensionAmount);

        VerifierClaimCalculationResponseDTO response = VerifierClaimCalculationResponseDTO.builder()
                .nppfNumber(contributionSummary != null ? contributionSummary.getNppfNumber() : null)
                .contributionStartDate(
                        contributionSummary != null
                                ? memberDetail.getPfJoiningDate()
                                : null)
                .rentalAdjustmentResult(rentalAdjustmentResult)
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
                .loanAdjustmentResult(loanAdjustmentResult)
                .noOfYearInService(serviceYears)
                .totalAmount(contributionSummary.getTotalBalance())
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
                .recommendedBenefitType(recommendedRefundType)
                .finalPayableAmount(finalPayableAmount)
                .forfeitedComponents(forfeitedComponents)
                .build();

        return ApiResponseDTO.success(response);
    }

    private BigDecimal safeAdd(BigDecimal a, BigDecimal b) {
        return (a != null ? a : BigDecimal.ZERO)
                .add(b != null ? b : BigDecimal.ZERO);
    }

    private RentalAdjustmentResultDto deductRental(
            String nppfNumber,
            BigDecimal totalPfAmount,
            BigDecimal totalPfInterestAmount,
            List<Long> ruleTypeIds) {

        BigDecimal remainingPayableAmount = safeAdd(totalPfAmount, totalPfInterestAmount);

        BigDecimal totalRentalAdjustedAmount = BigDecimal.ZERO;
        List<RentalAdjustmentDetailDto> adjustmentDetails = new ArrayList<>();

        if (nppfNumber == null || nppfNumber.isBlank()) {
            return buildEmptyRentalResult(
                    remainingPayableAmount,
                    "NPPF number is missing.");
        }

        if (ruleTypeIds == null || ruleTypeIds.isEmpty()) {
            return buildEmptyRentalResult(
                    remainingPayableAmount,
                    "No rental deduction rule found for this claim type.");
        }

        List<RentalDetailResponseDto> rentalDetails = rentalDetailService.getRentalDetails(nppfNumber).getData();

        if (rentalDetails == null || rentalDetails.isEmpty()) {
            return buildEmptyRentalResult(
                    remainingPayableAmount,
                    "No outstanding rental found.");
        }

        LocalDate today = LocalDate.now();

        List<RentalDeductionMapping> mappings = rentalDeductionMappingRepository.findByRuleType_IdIn(ruleTypeIds)
                .stream()
                .filter(Objects::nonNull)
                .filter(mapping -> mapping.getRentalType() != null)
                .filter(mapping -> mapping.getRentalType().getId() != null)
                .filter(mapping -> mapping.getEffectiveFrom() != null)
                .filter(mapping -> !mapping.getEffectiveFrom().isAfter(today))
                .filter(mapping -> mapping.getEffectiveTo() == null
                        || !mapping.getEffectiveTo().isBefore(today))
                .toList();

        if (mappings.isEmpty()) {
            return buildEmptyRentalResult(
                    remainingPayableAmount,
                    "No active rental deduction mapping found.");
        }

        Map<Long, RentalDeductionMapping> mappingByRentalTypeId = mappings.stream()
                .collect(Collectors.toMap(
                        mapping -> mapping.getRentalType().getId(),
                        mapping -> mapping,
                        (oldValue, newValue) -> oldValue));

        for (RentalDetailResponseDto rental : rentalDetails) {

            if (remainingPayableAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            if (rental == null
                    || rental.getRentalType() == null
                    || rental.getOutstandingAmount() == null
                    || rental.getOutstandingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            RentalMaster rentalMaster = rentalMasterRepository
                    .findByRentalTypeIgnoreCase(rental.getRentalType())
                    .orElse(null);

            if (rentalMaster == null) {
                continue;
            }

            RentalDeductionMapping mapping = mappingByRentalTypeId.get(rentalMaster.getId());

            if (mapping == null) {
                continue;
            }

            BigDecimal outstandingAmount = rental.getOutstandingAmount();

            // ✅ DIRECT SUBTRACTION - Use full outstanding amount instead of percentage
            BigDecimal adjustedAmount = remainingPayableAmount.min(outstandingAmount);

            remainingPayableAmount = remainingPayableAmount.subtract(adjustedAmount);

            totalRentalAdjustedAmount = totalRentalAdjustedAmount.add(adjustedAmount);

            adjustmentDetails.add(
                    RentalAdjustmentDetailDto.builder()
                            .rentalId(rentalMaster.getId())
                            .rentalName(rentalMaster.getRentalType())
                            .outstandingAmount(outstandingAmount)
                            .adjustedAmount(adjustedAmount)
                            .appliedPercentageAmount(BigDecimal.valueOf(100)) // ✅ 100% applied
                            .build());
        }

        if (adjustmentDetails.isEmpty()) {
            return buildEmptyRentalResult(
                    remainingPayableAmount != null ? remainingPayableAmount : BigDecimal.ZERO,
                    "No applicable outstanding rental found for deduction.");
        }

        return RentalAdjustmentResultDto.builder()
                .totalAdjustedAmount(totalRentalAdjustedAmount)
                .finalPayableAmount(remainingPayableAmount)
                .deductions(adjustmentDetails)
                .adjustmentNote(String.format(
                        "Rental adjusted directly. Total adjusted amount: %.2f.",
                        totalRentalAdjustedAmount))
                .build();
    }

    private RentalAdjustmentResultDto buildEmptyRentalResult(
            BigDecimal finalPayableAmount,
            String note) {
        return RentalAdjustmentResultDto.builder()
                .totalAdjustedAmount(BigDecimal.ZERO)
                .finalPayableAmount(finalPayableAmount)
                .deductions(Collections.emptyList())
                .adjustmentNote(note)
                .build();
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

    private VerifierEligibilityResultDto handleEligibilityRule(
            MatchedSubClaimRuleDto matchedRule,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary,
            List<VerifierClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

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

        return VerifierEligibilityResultDto.builder()
                .eligibleComponents(
                        eligible == null
                                ? Collections.emptyList()
                                : eligible)
                .build();
    }

    private LoanAdjustmentResultDto deductLoanByPriority(String nppfNumber, BigDecimal totalPfAmount,
            BigDecimal totalPfInterestAmount, BigDecimal finalPayableAmount, String recommendedRefundType,
            List<Long> ruleTypeIds) {

        // Calculate the total available amount for loan deduction
        BigDecimal totalAvailableAmount;
        if ("LUMPSUM".equals(recommendedRefundType)) {
            totalAvailableAmount = finalPayableAmount != null ? finalPayableAmount : BigDecimal.ZERO;
        } else {
            BigDecimal pfAmount = totalPfAmount != null ? totalPfAmount : BigDecimal.ZERO;
            BigDecimal pfInterest = totalPfInterestAmount != null ? totalPfInterestAmount : BigDecimal.ZERO;
            totalAvailableAmount = pfAmount.add(pfInterest);
        }

        System.out.println("=== LOAN DEDUCTION DEBUG ===");
        System.out.println("totalAvailableAmount: " + totalAvailableAmount);
        System.out.println("recommendedRefundType: " + recommendedRefundType);

        // Early validation checks
        if (nppfNumber == null || nppfNumber.isBlank()) {
            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(totalAvailableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("NPPF number is missing.")
                    .build();
        }

        if (ruleTypeIds == null || ruleTypeIds.isEmpty()) {
            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(totalAvailableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("No loan deduction rule found for this claim type.")
                    .build();
        }

        // Get loan details
        List<LoanDetailResponseDto> loanDetails = loanDetailService.getLoanDetails(nppfNumber).getData();
        System.out.println("Total loans from service: " + (loanDetails != null ? loanDetails.size() : 0));

        if (loanDetails == null || loanDetails.isEmpty()) {
            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(totalAvailableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("No outstanding loan found.")
                    .build();
        }

        LocalDate today = LocalDate.now();

        // Get loan type masters
        Map<String, LoanTypeMaster> loanTypeMasterMap = loanDetails.stream()
                .filter(Objects::nonNull)
                .filter(loan -> loan.getLoanName() != null)
                .map(loan -> loanTypeRepository.findByName(loan.getLoanName()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        loanType -> loanType.getName().trim().toUpperCase(),
                        loanType -> loanType,
                        (oldValue, newValue) -> oldValue));

        System.out.println("Loan type masters found: " + loanTypeMasterMap.size());

        // Get active mappings
        List<LoanDeductionMapping> mappings = loanDeductionMappingRepository.findByRuleType_IdIn(ruleTypeIds)
                .stream()
                .filter(Objects::nonNull)
                .filter(mapping -> mapping.getLoanType() != null)
                .filter(mapping -> mapping.getLoanType().getId() != null)
                .filter(mapping -> mapping.getLoanType().getName() != null)
                .filter(mapping -> mapping.getEffectiveFrom() != null)
                .filter(mapping -> !mapping.getEffectiveFrom().isAfter(today))
                .filter(mapping -> mapping.getEffectiveTo() == null
                        || !mapping.getEffectiveTo().isBefore(today))
                .toList();

        System.out.println("Active mappings found: " + mappings.size());

        if (mappings.isEmpty()) {
            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(totalAvailableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("No active loan deduction mapping found.")
                    .build();
        }

        Map<String, LoanDeductionMapping> mappingMap = mappings.stream()
                .collect(Collectors.toMap(
                        mapping -> mapping.getLoanType().getName().trim().toUpperCase(),
                        mapping -> mapping,
                        (oldValue, newValue) -> oldValue));

        System.out.println("Mapping map size: " + mappingMap.size());
        System.out.println("Mapping keys: " + mappingMap.keySet());

        // Sort loans by priority
        List<LoanDetailResponseDto> sortedLoanDetails = loanDetails.stream()
                .filter(Objects::nonNull)
                .filter(loan -> loan.getOutstandingAmount() != null)
                .filter(loan -> loan.getOutstandingAmount().compareTo(BigDecimal.ZERO) > 0)
                .filter(loan -> loan.getLoanName() != null)
                .filter(loan -> mappingMap.containsKey(loan.getLoanName().trim().toUpperCase()))
                .sorted(Comparator.comparing(loan -> {
                    LoanDeductionMapping mapping = mappingMap.get(loan.getLoanName().trim().toUpperCase());
                    return mapping.getPriorityOrder() != null
                            ? mapping.getPriorityOrder()
                            : Integer.MAX_VALUE;
                }))
                .toList();

        System.out.println("Sorted loans to process: " + sortedLoanDetails.size());

        for (LoanDetailResponseDto loan : sortedLoanDetails) {
            System.out.println("  Loan: " + loan.getLoanName() + ", Outstanding: " + loan.getOutstandingAmount());
        }

        if (sortedLoanDetails.isEmpty()) {
            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(totalAvailableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("No applicable outstanding loan found for deduction.")
                    .build();
        }

        // Process loans in priority order
        BigDecimal remainingPayableAmount = totalAvailableAmount;
        BigDecimal totalLoanAdjustedAmount = BigDecimal.ZERO;
        List<LoanAdjustmentDetailDto> adjustmentDetails = new ArrayList<>();

        System.out.println("Starting loop with remainingPayableAmount: " + remainingPayableAmount);

        for (LoanDetailResponseDto loan : sortedLoanDetails) {
            System.out.println("--- Processing loan: " + loan.getLoanName() + " ---");

            BigDecimal outstandingAmount = loan.getOutstandingAmount();
            BigDecimal adjustedAmount;

            System.out.println("  Outstanding: " + outstandingAmount);
            System.out.println("  Remaining payable before: " + remainingPayableAmount);

            // Check if we have enough money to pay this loan
            if (remainingPayableAmount.compareTo(BigDecimal.ZERO) <= 0) {
                adjustedAmount = BigDecimal.ZERO;
                System.out.println("  No money left, adjustedAmount = 0");
            } else {
                // Deduct as much as possible from this loan
                adjustedAmount = remainingPayableAmount.min(outstandingAmount);
                remainingPayableAmount = remainingPayableAmount.subtract(adjustedAmount);
                totalLoanAdjustedAmount = totalLoanAdjustedAmount.add(adjustedAmount);
                System.out.println("  Adjusted: " + adjustedAmount);
                System.out.println("  Remaining payable after: " + remainingPayableAmount);
            }

            BigDecimal remainingOutstandingAmount = outstandingAmount.subtract(adjustedAmount);
            LoanDeductionMapping mapping = mappingMap.get(loan.getLoanName().trim().toUpperCase());

            // Determine status
            String status;
            if (adjustedAmount.compareTo(BigDecimal.ZERO) == 0) {
                status = "NOT_ADJUSTABLE";
            } else if (remainingOutstandingAmount.compareTo(BigDecimal.ZERO) == 0) {
                status = "FULLY_ADJUSTABLE";
            } else {
                status = "PARTIALLY_ADJUSTABLE";
            }

            System.out.println("  Status: " + status);
            System.out.println("  Remaining outstanding: " + remainingOutstandingAmount);

            adjustmentDetails.add(
                    LoanAdjustmentDetailDto.builder()
                            .loanTypeId(mapping.getLoanType().getId())
                            .loanTypeName(mapping.getLoanType().getName())
                            .priorityOrder(mapping.getPriorityOrder() != null
                                    ? mapping.getPriorityOrder()
                                    : Integer.MAX_VALUE)
                            .outstandingAmount(outstandingAmount)
                            .adjustedAmount(adjustedAmount)
                            .remainingOutstandingAmount(remainingOutstandingAmount)
                            .status(status)
                            .build());
        }

        System.out.println("=== FINAL RESULT ===");
        System.out.println("Total adjusted: " + totalLoanAdjustedAmount);
        System.out.println("Final payable: " + remainingPayableAmount);
        System.out.println("Number of deductions: " + adjustmentDetails.size());

        return LoanAdjustmentResultDto.builder()
                .totalAdjustedAmount(totalLoanAdjustedAmount)
                .finalPayableAmount(remainingPayableAmount)
                .deductions(adjustmentDetails)
                .adjustmentNote(String.format(
                        "Loan adjusted by priority. Total adjusted amount: %.2f.",
                        totalLoanAdjustedAmount))
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
        return ruleCode != null
                && ruleCode.toUpperCase().contains("VESTING");
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

    private VerifierLapsedResultDto handleLapsedRule(
            MatchedSubClaimRuleDto matchedRule,
            ClaimInitialPreviewRequest request,
            MemberContributionSummary contributionSummary,
            List<VerifierClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

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
            return VerifierLapsedResultDto.builder()
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

        return VerifierLapsedResultDto.builder()
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
            List<VerifierClaimCalculationResponseDTO.ExpressionCalculationDTO> expressionCalculations) {

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

            List<String> resolvedCodes = resolveExpressionComponentCodes(
                    expressionDto.getExpression(),
                    matchedRule.getComponentMapping(),
                    componentAmountMap);

            // Calculate expression amount from resolved codes
            BigDecimal expressionAmount = resolvedCodes.stream()
                    .map(code -> componentAmountMap.getOrDefault(code, BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Build expression calculation DTO
            if (expressionCalculations != null) {
                expressionCalculations.add(
                        VerifierClaimCalculationResponseDTO.ExpressionCalculationDTO.builder()
                                .expression(expressionDto.getExpression())
                                .resolvedCodes(resolvedCodes)
                                .expressionAmount(expressionAmount)
                                .type(calculationType)
                                .build());
            }

            // Add individual component balances
            for (String componentCode : resolvedCodes) {

                BigDecimal amount = componentAmountMap.getOrDefault(
                        componentCode,
                        BigDecimal.ZERO);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                results.add(
                        ComponentBalanceDTO.builder()
                                .subRuleCode(matchedRule.getSubClaimCode())
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

        if (matchedRule == null || matchedRule.getComponentMapping() == null) {
            return Collections.emptyList();
        }

        var mapping = matchedRule.getComponentMapping();

        List<String> codes = new ArrayList<>();

        if (isYes(mapping.getHasPfMc())) {
            codes.add("PF_MC");
        }

        if (isYes(mapping.getHasPfEc())) {
            codes.add("PF_EC");
        }

        if (isYes(mapping.getHasPfImc())) {
            codes.add("PF_IMC");
        }

        if (isYes(mapping.getHasPfIec())) {
            codes.add("PF_IEC");
        }

        if (isYes(mapping.getHasPMc())) {
            codes.add("P_MC");
        }

        if (isYes(mapping.getHasPEc())) {
            codes.add("P_EC");
        }

        if (isYes(mapping.getHasPImc())) {
            codes.add("P_IMC");
        }

        if (isYes(mapping.getHasPIec())) {
            codes.add("P_IEC");
        }

        if (isYes(mapping.getHasGc())) {
            codes.add("GC");
        }

        if (isYes(mapping.getHasGic())) {
            codes.add("GIC");
        }

        if (isYes(mapping.getHasVc())) {
            codes.add("VC");
        }

        if (isYes(mapping.getHasVic())) {
            codes.add("VIC");
        }

        if (isYes(mapping.getHasIvc())) {
            codes.add("IVC");
        }

        if (isYes(mapping.getHasIgc())) {
            codes.add("IGC");
        }

        return codes.stream()
                .distinct()
                .toList();
    }

    private boolean isYes(String value) {
        return "Y".equalsIgnoreCase(value);
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

            // ✅ Add principal amount (if > 0)
            if (component.getPrincipalAmount() != null &&
                    component.getPrincipalAmount().compareTo(BigDecimal.ZERO) > 0) {
                map.put(code, component.getPrincipalAmount());
                System.out.println("  ✅ Added principal: " + code + " = " + component.getPrincipalAmount());
            }

            // ✅ Add interest amount (if > 0) - THIS IS THE KEY FIX
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
}