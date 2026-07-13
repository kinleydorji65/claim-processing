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
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.entities.common.RentalMaster;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.CaseTypeEnum;
import com.claim.claim_processing.common.entities.pension.PensionDetail;
import com.claim.claim_processing.common.repository.adjustmentMaster.LoanTypeRepository;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.repository.common.RentalMasterRepository;
import com.claim.claim_processing.common.repository.pension.PensionDetailRepository;
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
import com.claim.claim_processing.rule.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.claim.DTO.response.EligibilityResultDto;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedResultDto;
import com.claim.claim_processing.rule.claim.DTO.response.LoanAdjustmentDetailDto;
import com.claim.claim_processing.rule.claim.DTO.response.RentalAdjustmentDetailDto;
import com.claim.claim_processing.rule.claim.DTO.response.SpecialCasePreviewResponse;
import com.claim.claim_processing.rule.claim.DTO.response.SpecialCasePreviewResponse.ForfeitedComponentClaim;
import com.claim.claim_processing.rule.claim.DTO.response.SpecialCasePreviewResponse.PensionToLumpSumConversion;
import com.claim.claim_processing.rule.claim.DTO.response.VestingResultDto;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleProcessing.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.LoanDeductionMapping;
import com.claim.claim_processing.rule.ruleProcessing.entities.rule.RentalDeductionMapping;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.LoanDeductionMappingRepository;
import com.claim.claim_processing.rule.ruleProcessing.repositories.rule.RentalDeductionMappingRepository;
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
    private final MemberService memberService;
    private final RentalDetailService rentalDetailService;
    private final RentalDeductionMappingRepository rentalDeductionMappingRepository;
    private final RentalMasterRepository rentalMasterRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final PensionDetailRepository pensionDetailRepository;
    private final ReserveAccountRepository reserveAccountRepository;

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
        if (isLoanApply) {
            loanAdjustmentResult = deductLoanByPriority(
                    request.getNppfNumber(),
                    finalPayableAmount, ruleTypeIds);

            if (loanAdjustmentResult != null) {
                finalPayableAmount = loanAdjustmentResult.getFinalPayableAmount();
            }
        }

        if (isRentalApply) {
            rentalAdjustmentResult = deductRental(
                    request.getNppfNumber(),
                    finalPayableAmount, ruleTypeIds);

            if (rentalAdjustmentResult != null) {
                finalPayableAmount = rentalAdjustmentResult.getFinalPayableAmount();
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

        ClaimCalculationResponseDTO response = ClaimCalculationResponseDTO.builder()
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
                .totalAmount(totalAmount)
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
                .forfeitedComponents(forfeitedComponents)

                .build();

        return ApiResponseDTO.success(response);
    }

    private RentalAdjustmentResultDto deductRental(
            String nppfNumber,
            BigDecimal availableAmount,
            List<Long> ruleTypeIds) {

        BigDecimal remainingPayableAmount = availableAmount != null ? availableAmount : BigDecimal.ZERO;

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

            BigDecimal deductionPercentage = mapping.getPercentage() != null
                    ? mapping.getPercentage()
                    : BigDecimal.valueOf(100);

            BigDecimal allowedDeductionAmount = outstandingAmount
                    .multiply(deductionPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal adjustedAmount = remainingPayableAmount.min(allowedDeductionAmount);

            remainingPayableAmount = remainingPayableAmount.subtract(adjustedAmount);

            totalRentalAdjustedAmount = totalRentalAdjustedAmount.add(adjustedAmount);

            adjustmentDetails.add(
                    RentalAdjustmentDetailDto.builder()
                            .rentalId(rentalMaster.getId())
                            .rentalName(rentalMaster.getRentalType())
                            .outstandingAmount(outstandingAmount)
                            .adjustedAmount(adjustedAmount)
                            .appliedPercentageAmount(deductionPercentage)
                            .build());
        }

        if (adjustmentDetails.isEmpty()) {
            return buildEmptyRentalResult(
                    availableAmount != null ? availableAmount : BigDecimal.ZERO,
                    "No applicable outstanding rental found for deduction.");
        }

        return RentalAdjustmentResultDto.builder()
                .totalAdjustedAmount(totalRentalAdjustedAmount)
                .finalPayableAmount(remainingPayableAmount)
                .deductions(adjustmentDetails)
                .adjustmentNote(
                        "Rental adjusted. Total adjusted amount: "
                                + totalRentalAdjustedAmount
                                + ". Final payable amount: "
                                + remainingPayableAmount)
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

    private LoanAdjustmentResultDto deductLoanByPriority(String nppfNumber, BigDecimal availableAmount,
            List<Long> ruleTypeIds) {

        BigDecimal remainingPayableAmount = availableAmount != null ? availableAmount : BigDecimal.ZERO;

        BigDecimal totalLoanAdjustedAmount = BigDecimal.ZERO;
        List<LoanAdjustmentDetailDto> adjustmentDetails = new ArrayList<>();

        if (nppfNumber == null || nppfNumber.isBlank()) {
            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(remainingPayableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("NPPF number is missing.")
                    .build();
        }

        if (ruleTypeIds == null || ruleTypeIds.isEmpty()) {
            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(remainingPayableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("No loan deduction rule found for this claim type.")
                    .build();
        }

        List<LoanDetailResponseDto> loanDetails = loanDetailService.getLoanDetails(nppfNumber).getData();

        if (loanDetails == null || loanDetails.isEmpty()) {
            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(remainingPayableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("No outstanding loan found.")
                    .build();
        }

        LocalDate today = LocalDate.now();
        Map<String, LoanTypeMaster> loanTypeMasterMap = loanDetails.stream()
                .filter(Objects::nonNull)
                .filter(loan -> loan.getLoanName() != null)
                .map(loan -> loanTypeRepository.findByName(loan.getLoanName()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        loanType -> loanType.getName().trim().toUpperCase(),
                        loanType -> loanType,
                        (oldValue, newValue) -> oldValue));

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

        if (mappings.isEmpty()) {
            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(remainingPayableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("No active loan deduction mapping found.")
                    .build();
        }

        Map<String, LoanDeductionMapping> mappingMap = mappings.stream()
                .collect(Collectors.toMap(
                        mapping -> mapping.getLoanType().getName().trim().toUpperCase(),
                        mapping -> mapping,
                        (oldValue, newValue) -> oldValue));

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

        if (sortedLoanDetails.isEmpty()) {
            return LoanAdjustmentResultDto.builder()
                    .totalAdjustedAmount(BigDecimal.ZERO)
                    .finalPayableAmount(remainingPayableAmount)
                    .deductions(Collections.emptyList())
                    .adjustmentNote("No applicable outstanding loan found for deduction.")
                    .build();
        }

        for (LoanDetailResponseDto loan : sortedLoanDetails) {

            if (remainingPayableAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal outstandingAmount = loan.getOutstandingAmount();
            BigDecimal adjustedAmount = remainingPayableAmount.min(outstandingAmount);
            BigDecimal remainingOutstandingAmount = outstandingAmount.subtract(adjustedAmount);

            remainingPayableAmount = remainingPayableAmount.subtract(adjustedAmount);
            totalLoanAdjustedAmount = totalLoanAdjustedAmount.add(adjustedAmount);

            LoanDeductionMapping mapping = mappingMap.get(loan.getLoanName().trim().toUpperCase());

            adjustmentDetails.add(
                    LoanAdjustmentDetailDto.builder()
                            .loanTypeId(mapping.getLoanType().getId())
                            .loanTypeName(mapping.getLoanType().getName())
                            .priorityOrder(
                                    mapping.getPriorityOrder() != null
                                            ? mapping.getPriorityOrder()
                                            : Integer.MAX_VALUE)
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
                    matchedRule.getComponentMapping(), componentAmountMap);

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
            return map;
        }

        for (MemberContributionSummary.ComponentGroup component : contributionSummary.getComponentGroups()) {

            if (component == null || component.getComponentCode() == null) {
                continue;
            }

            String code = component.getComponentCode().trim().toUpperCase();
            BigDecimal principal = component.getPrincipalAmount() == null ? BigDecimal.ZERO
                    : component.getPrincipalAmount();
            BigDecimal interest = component.getInterestAmount() == null ? BigDecimal.ZERO
                    : component.getInterestAmount();

            // Add principal amounts
            map.put(code, principal); // Original code
            map.put("PF_" + code, principal); // With PF_ prefix for PF components
            map.put("P_" + code, principal); // With P_ prefix for Pension components

            // Map specific codes based on component type
            switch (code) {
                case "IEC":
                    // Employee PF Contribution (Principal)
                    map.put("PF_EC", principal);
                    // Interest on Employee PF Contribution
                    map.put("PF_IEC", interest);
                    map.put("IEC", principal);
                    break;

                case "IMC":
                    // Employer PF Contribution (Principal)
                    map.put("PF_MC", principal);
                    // Interest on Employer PF Contribution
                    map.put("PF_IMC", interest);
                    map.put("IMC", principal);
                    break;

                case "IPC":
                    // Employee Pension Contribution (Principal)
                    map.put("P_EC", principal);
                    // Interest on Employee Pension Contribution
                    map.put("P_IEC", interest);
                    map.put("IPC", principal);
                    break;

                case "IGC":
                    // Government Contribution
                    map.put("GC", principal);
                    map.put("IGC", principal);
                    break;

                case "IVC":
                    // Voluntary Contribution
                    map.put("VC", principal);
                    map.put("IVC", principal);
                    break;

                default:
                    // Handle any other codes
                    map.put(code, principal);
                    break;
            }

            // Also add interest amounts with proper codes
            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                String interestCode = "I" + code;
                map.put(interestCode, interest);

                // Map specific interest codes
                switch (code) {
                    case "IEC":
                        map.put("PF_IEC", interest);
                        break;
                    case "IMC":
                        map.put("PF_IMC", interest);
                        break;
                    case "IPC":
                        map.put("P_IEC", interest);
                        break;
                }
            }
        }

        // Debug: Print the map
        System.out.println("========== COMPONENT MAP ==========");
        for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
            System.out.println("  " + entry.getKey() + " = " + entry.getValue());
        }

        return map;
    }

    public ApiResponseDTO<Object> getSpecialCaseBenefit(CaseTypeEnum caseType, String nppfNumber) {
        if (caseType == null || nppfNumber == null || nppfNumber.isBlank()) {
            return ApiResponseDTO.notFound("Case type or NPPF number is missing.");
        }

        if (caseType.toString().equals("NORMAL_CLAIM_FORFEITED")) {
            ClaimCalculationResponseDTO response = calculateSpecialCaseBenefit(nppfNumber);
            if (response == null) {
                return ApiResponseDTO.success("No Detail Found with nppf number: " + nppfNumber);
            }
            return ApiResponseDTO.success(response);
        }
        PensionToLumpSumConversion pensionDetail = new PensionToLumpSumConversion();
        ForfeitedComponentClaim forfeitedDetail = new ForfeitedComponentClaim();
        if (caseType.toString().equals("CONVERSION_FROM_PENSION_TO_LUMSUM")) {
            pensionDetail = getPensionDetail(nppfNumber);
        }

        // Fix 2: CLAIM_FORFEITED_COMPONENT
        if (caseType.toString().equals("SPECIAL_NORMAL_CLAIM")) {
            forfeitedDetail = getForfeitedComponentDetail(nppfNumber);
        }
        SpecialCasePreviewResponse response = SpecialCasePreviewResponse.builder()
                .caseType(caseType.toString())
                .calculationPreview(SpecialCasePreviewResponse.BenefitCalculationPreview.builder()
                        .pensionToLumpSum(pensionDetail)
                        .forfeitedComponentClaim(forfeitedDetail)
                        .build())
                .build();
        return ApiResponseDTO.success(response);
    }

    private PensionToLumpSumConversion getPensionDetail(String nppfNumber) {
        PensionDetail pensionDetail = pensionDetailRepository.findByNppfNumber(nppfNumber).orElse(new PensionDetail());
        if (pensionDetail == null) {
            return null;
        }
        return PensionToLumpSumConversion.builder()
                .pensionDetailId(pensionDetail.getId())
                .totalContributionMonths(pensionDetail.getTotalContributionMonths())
                .totalContributionYears(pensionDetail.getTotalContributionYears())
                .pensionType(pensionDetail.getPensionType())
                .pensionStartDate(pensionDetail.getPensionStartDate())
                .totalPensionAmount(pensionDetail.getTotalPensionFund())
                .bankTypeId(pensionDetail.getBankTypeId())
                .bankName(pensionDetail.getBankName())
                .identityNumber(pensionDetail.getMemberIdentityNumber())
                .accountHolderName(pensionDetail.getAccountHolderName())
                .bankAccountNumber(pensionDetail.getBankAccountNumber())
                .ifscCode(pensionDetail.getIfscCode())
                .build();
    }

    private ForfeitedComponentClaim getForfeitedComponentDetail(String nppfNumber) {
        ReserveAccount reserveAccount = reserveAccountRepository.findByNppfNumber(nppfNumber)
                .orElse(new ReserveAccount());
        if (reserveAccount == null) {
            return null;
        }

        return ForfeitedComponentClaim.builder()
                .reserveAccountId(reserveAccount.getId())
                .totalForfeitedAmount(reserveAccount.getForfeitedAmount())
                .eligibleClaimAmount(reserveAccount.getForfeitedAmount())
                .forfeitedDate(reserveAccount.getCreatedAt())
                .componentCodes(reserveAccount.getComponentCodes())
                .build();
    }

    private ClaimCalculationResponseDTO calculateSpecialCaseBenefit(String nppfNumber) {

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

        // 4. Build all components as eligible (no rules applied)
        List<ComponentBalanceDTO> allComponents = buildAllEligibleComponents(contributionSummary);

        // 5. Calculate totals using the same logic as your main service
        BigDecimal totalPfAmount = BigDecimal.ZERO;
        BigDecimal backupTotalPfAmount = BigDecimal.ZERO;
        BigDecimal totalPensionAmount = BigDecimal.ZERO;
        BigDecimal backupTotalPensionAmount = BigDecimal.ZERO;
        BigDecimal totalPfInterestAmount = BigDecimal.ZERO;
        BigDecimal backupTotalPfInterestAmount = BigDecimal.ZERO;
        BigDecimal totalPensionInterestAmount = BigDecimal.ZERO;
        BigDecimal backupTotalPensionInterestAmount = BigDecimal.ZERO;

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
                case "PF_GC":
                case "PF_VC":
                    totalPfAmount = totalPfAmount.add(amount);
                    backupTotalPfAmount = backupTotalPfAmount.add(amount);
                    break;

                case "PF_IMC":
                case "PF_IEC":
                case "PF_GIC":
                case "PF_VIC":
                    totalPfInterestAmount = totalPfInterestAmount.add(amount);
                    backupTotalPfInterestAmount = backupTotalPfInterestAmount.add(amount);
                    break;

                case "P_MC":
                case "P_EC":
                case "PC_MC":
                case "PC_EC":
                    totalPensionAmount = totalPensionAmount.add(amount);
                    backupTotalPensionAmount = backupTotalPensionAmount.add(amount);
                    break;

                case "P_IMC":
                case "P_IEC":
                case "PC_IMC":
                case "PC_IEC":
                    totalPensionInterestAmount = totalPensionInterestAmount.add(amount);
                    backupTotalPensionInterestAmount = backupTotalPensionInterestAmount.add(amount);
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
        LocalDate endDate = LocalDate.now(); // Assuming current date as end date for service years calculation

        BigDecimal serviceYears = calculateServiceYears(joiningDate, endDate);

        // 9. Build response
        ClaimCalculationResponseDTO response = ClaimCalculationResponseDTO.builder()
                .nppfNumber(contributionSummary.getNppfNumber())
                .contributionStartDate(memberDetail.getPfJoiningDate())
                .contributionEndDate(contributionSummary.getContributionEndDate())
                .totalContributionMonths(contributionSummary.getTotalContributionMonths())
                .totalNonContributionMonths(contributionSummary.getTotalNonContributionMonths())
                .noOfYearInService(serviceYears)
                .totalAmount(totalAmount)
                .components(allComponents)
                .loanCheck(false) // No loan deduction
                .rentalCheck(false) // No rental deduction
                .loanAdjustmentResult(null)
                .rentalAdjustmentResult(null)
                .totalPfAmount(backupTotalPfAmount)
                .totalPensionAmount(backupTotalPensionAmount)
                .totalPfInterestAmount(backupTotalPfInterestAmount)
                .totalPensionInterestAmount(backupTotalPensionInterestAmount)
                .pfIsEligible(backupTotalPfAmount.compareTo(BigDecimal.ZERO) > 0
                        ? EligibilityEnum.ELIGIBLE
                        : EligibilityEnum.NOT_ELIGIBLE)
                .pensionIsEligible(backupTotalPensionAmount.compareTo(BigDecimal.ZERO) > 0
                        ? EligibilityEnum.ELIGIBLE
                        : EligibilityEnum.NOT_ELIGIBLE)
                .finalPayableAmount(finalPayableAmount)
                .forfeitedComponents(Collections.emptyList()) // No forfeited components
                .vestingNote("Special Case: All components are eligible for lump sum withdrawal")
                .recommendedBenefitType("Lump Sum")
                .eligibilityNote("Special Case: All contributions are eligible")
                .expressionCalculations(Collections.emptyList()) // No expressions in special case
                .build();

        return response;
    }

    /**
     * Build all components as eligible (no rules applied)
     * This gets all components from the contribution summary
     */
    private List<ComponentBalanceDTO> buildAllEligibleComponents(MemberContributionSummary contributionSummary) {
        List<ComponentBalanceDTO> components = new ArrayList<>();

        if (contributionSummary.getComponentGroups() == null) {
            return components;
        }

        for (MemberContributionSummary.ComponentGroup component : contributionSummary.getComponentGroups()) {
            if (component == null || component.getComponentCode() == null) {
                continue;
            }

            String code = component.getComponentCode().trim().toUpperCase();

            // Get principal (contribution) amount
            BigDecimal principal = component.getPrincipalAmount() != null
                    ? component.getPrincipalAmount()
                    : BigDecimal.ZERO;

            // Get interest amount
            BigDecimal interest = component.getInterestAmount() != null
                    ? component.getInterestAmount()
                    : BigDecimal.ZERO;

            // Add principal component
            if (principal.compareTo(BigDecimal.ZERO) > 0) {
                components.add(ComponentBalanceDTO.builder()
                        .code(code)
                        .name(getComponentName(code))
                        .type("CONTRIBUTION")
                        .amount(principal)
                        .build());
            }

            // Add interest component
            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                String interestCode = "I" + code;
                components.add(ComponentBalanceDTO.builder()
                        .code(interestCode)
                        .name(getComponentName(interestCode))
                        .type("INTEREST")
                        .amount(interest)
                        .build());
            }
        }

        return components;
    }

    /**
     * Get component name
     */
    private String getComponentName(String code) {
        if (code == null)
            return code;

        Map<String, String> nameMap = Map.ofEntries(
                Map.entry("PF_MC", "Member's Contribution to PF"),
                Map.entry("PF_EC", "Employer's Contribution to PF"),
                Map.entry("PF_IMC", "Interest on Member's PF Contribution"),
                Map.entry("PF_IEC", "Interest on Employer's PF Contribution"),
                Map.entry("PF_GC", "Government Contribution to PF"),
                Map.entry("PF_IGC", "Interest on Government PF Contribution"),
                Map.entry("P_MC", "Member's Pension Contribution"),
                Map.entry("P_EC", "Employer's Pension Contribution"),
                Map.entry("P_IMC", "Interest on Member's Pension"),
                Map.entry("P_IEC", "Interest on Employer's Pension"),
                Map.entry("PC_MC", "Member's Pension Contribution"),
                Map.entry("PC_EC", "Employer's Pension Contribution"),
                Map.entry("PC_IMC", "Interest on Member's Pension"),
                Map.entry("PC_IEC", "Interest on Employer's Pension"));

        return nameMap.getOrDefault(code, code);
    }

}