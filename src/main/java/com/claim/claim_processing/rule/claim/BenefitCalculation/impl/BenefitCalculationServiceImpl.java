package com.claim.claim_processing.rule.claim.BenefitCalculation.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.mapper.common.RuleTypeMapper;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.rule.EligibleEnum.EligibilityEnum;
import com.claim.claim_processing.rule.claim.BenefitCalculation.BenefitCalculationService;
import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.request.ClaimPreviewRequest;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimCalculationResponseDTO.ComponentBalanceDTO;
import com.claim.claim_processing.rule.claim.DTO.response.ClaimEligibilityPreviewResponse;
import com.claim.claim_processing.rule.claim.DTO.response.LapsedRefundPreviewResponseDTO;
import com.claim.claim_processing.rule.claim.DTO.response.VestingRuleResponseDTO;
import com.claim.claim_processing.rule.claim.eligibility.service.ClaimEligibilityRuleService;
import com.claim.claim_processing.rule.claim.eligibility.service.LapsedRefundService;
import com.claim.claim_processing.rule.claim.vesting.service.VestingRuleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BenefitCalculationServiceImpl implements BenefitCalculationService {

        private final MemberContributionService memberContributionService;
        private final ClaimEligibilityRuleService claimEligibilityRuleService;
        private final LapsedRefundService lapsedRefundService;
        private final VestingRuleService vestingRuleService;
        private final ClaimTypeRuleMapRepository claimTypeRuleMapRepository;
        private final RuleTypeMapper ruleTypeMapper;


        public ApiResponseDTO<ClaimCalculationResponseDTO> calculateBenefit(ClaimPreviewRequest request) {
                List<RuleTypeResponseDto> ruleTypes = checkEligibleRules(request.getClaimTypeId());
                MemberContributionSummary contributionSummary = memberContributionService
                                .getContributionSummary(request.getMemberCode());
                ClaimEligibilityPreviewResponse claimEligibilityPreviewResponse = null;
                LapsedRefundPreviewResponseDTO previewLapsedRefund = null;
                VestingRuleResponseDTO vestingResponse = null;
                if(!ruleTypes.isEmpty() && ruleTypes.stream().anyMatch(rt -> rt.getCode().equals("ELIGIBILITY"))){
                        claimEligibilityPreviewResponse = claimEligibilityRuleService.previewEligibility(request);
                        previewLapsedRefund = lapsedRefundService.previewLapsedRefund(request);
                }
                if(!ruleTypes.isEmpty() && ruleTypes.stream().anyMatch(rt -> rt.getCode().equals("VESTING"))){
                        vestingResponse = vestingRuleService.determineVestingEligibility(request);

                }
        
                BigDecimal serviceYears = calculateServiceYears(
                                contributionSummary.getContributionStartDate(),
                                contributionSummary.getContributionEndDate());
                
                ClaimCalculationResponseDTO response = processComponentsWithRules(
                                contributionSummary,
                                vestingResponse,
                                claimEligibilityPreviewResponse,
                                previewLapsedRefund,
                                serviceYears, ruleTypes);
                return ApiResponseDTO.success(response);
        }

        private ClaimCalculationResponseDTO processComponentsWithRules(MemberContributionSummary contributionSummary,
                        VestingRuleResponseDTO vestingResponse, ClaimEligibilityPreviewResponse eligibilityResponse,
                        LapsedRefundPreviewResponseDTO lapsedResponse, BigDecimal serviceYears, List<RuleTypeResponseDto> ruleTypes) {
                Boolean pfEligible = false;
                Boolean pensionEligible = false;
                BigDecimal totalPfAmount = BigDecimal.ZERO;
                BigDecimal totalPensionAmount = BigDecimal.ZERO;
                BigDecimal totalPfInterestAmount = BigDecimal.ZERO;
                BigDecimal totalPensionInterestAmount = BigDecimal.ZERO;
                List<ClaimCalculationResponseDTO.ComponentBalanceDTO> components = Collections.emptyList();
                // 1. Collect rule component codes
                if (eligibilityResponse != null && vestingResponse != null) {
                        
                
                Set<String> validCodes = collectEligibilityComponent(eligibilityResponse);
                Set<String> forfeitedComponents = collectLapsedComponent(lapsedResponse);
                Set<String> vestedComponents = collectVestingComponent(vestingResponse);
                
                Set<String> allValidCodes = new HashSet<>(validCodes);

                allValidCodes.addAll(vestedComponents);
                if (forfeitedComponents != null) {
                        allValidCodes.removeAll(forfeitedComponents);
                }
                // if (vestingResponse.getPayoutResult())
                List<MemberContributionSummary.ComponentGroup> groups = contributionSummary.getComponentGroups();

                // 2. Filter contribution components
                components = groups
                                .stream()
                                .filter(cg -> allValidCodes.contains(cg.getCode()))
                                .map((MemberContributionSummary.ComponentGroup cg) -> ComponentBalanceDTO.builder()
                                                .code(cg.getCode())
                                                .name(cg.getName())
                                                .type(cg.getCode().contains("I") ? "INTEREST" : "CONTRIBUTION")
                                                .amount(cg.getPrincipal())
                                                .build())
                                .toList();

                pfEligible = components.stream()
                                .filter(c -> c.getCode().startsWith("PF_") && !c.getCode().contains("I"))
                                .findFirst()
                                .isPresent();
                pensionEligible = components.stream()
                                .filter(c -> c.getCode().startsWith("PC_") && !c.getCode().contains("I"))
                                .findFirst()
                                .isPresent();

                totalPfAmount = components.stream()
                                .filter(c -> c.getCode().startsWith("PF_"))
                                .filter(c -> !c.getCode().contains("I"))
                                .map(ClaimCalculationResponseDTO.ComponentBalanceDTO::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalPensionAmount = components.stream()
                                .filter(c -> c.getCode().startsWith("PC_"))
                                .filter(c -> !c.getCode().contains("I"))
                                .map(ClaimCalculationResponseDTO.ComponentBalanceDTO::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalPfInterestAmount = groups.stream()
                                .filter(g -> g.getCode().startsWith("PF_"))
                                .filter(g -> g.getCode().contains("I"))
                                .map(MemberContributionSummary.ComponentGroup::getTotalBalance)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalPensionInterestAmount = groups.stream()
                                .filter(g -> g.getCode().startsWith("PC_"))
                                .filter(g -> g.getCode().contains("I")) // include interest
                                .map(MemberContributionSummary.ComponentGroup::getTotalBalance)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        Boolean loanCheck = ruleTypes.stream().anyMatch(rt -> rt.getCode().equals("LOAN_ADJUSTMENT"));
        Boolean rentalCheck = ruleTypes.stream().anyMatch(rt -> rt.getCode().equals("RENTAL_ADJUSTMENT"));
                return ClaimCalculationResponseDTO.builder()
                                .nppfNumber(contributionSummary.getNppfNumber())
                                .contributionStartDate(contributionSummary.getContributionStartDate())
                                .contributionEndDate(contributionSummary.getContributionEndDate())
                                .totalContributionMonths(contributionSummary.getTotalContributionMonths())
                                .totalNonContributionMonths(contributionSummary.getTotalNonContributionMonths())
                                .pfIsEligible(pfEligible ? EligibilityEnum.ELIGIBLE : EligibilityEnum.NOT_ELIGIBLE)
                                .pensionIsEligible(pensionEligible ? EligibilityEnum.ELIGIBLE
                                                : EligibilityEnum.NOT_ELIGIBLE)
                                .totalPfAmount(totalPfAmount)
                                .totalPensionAmount(totalPensionAmount)
                                .totalPfInterestAmount(totalPfInterestAmount)
                                .totalPensionInterestAmount(totalPensionInterestAmount)
                                .noOfYearInService(serviceYears)
                                .loanCheck(loanCheck)
                                .rentalCheck(rentalCheck)
                                .eligibilityNote(vestingResponse.getEligibilityNote())
                                .components(components)
                                .build();
        }
        

        private Set<String> collectEligibilityComponent(ClaimEligibilityPreviewResponse eligibility) {
                if (eligibility == null || eligibility.getEligibleBenefits() == null) {
                        return Collections.emptySet();
                }
                Set<String> codes = new HashSet<>();
                codes.addAll(extractCodes(eligibility.getEligibleBenefits()));
                return codes;

        }

        private Set<String> collectLapsedComponent(LapsedRefundPreviewResponseDTO lapsed) {
                if (lapsed == null || lapsed.getLapsedBenefits() == null) {
                        return Collections.emptySet();
                }
                Set<String> codes = new HashSet<>();
                codes.addAll(extractCodes(lapsed.getLapsedBenefits()));
                return codes;

        }

        private Set<String> collectVestingComponent(VestingRuleResponseDTO vesting) {
                if (vesting == null) {
                        return Collections.emptySet();
                }
                Set<String> codes = new HashSet<>();
                codes.addAll(extractCodes(vesting.getCategoryBenefits()));
                return codes;

        }

        private BigDecimal calculateServiceYears(LocalDate startDate, LocalDate endDate) {
                if (startDate == null || endDate == null) {
                        return BigDecimal.ZERO;
                }
                long days = ChronoUnit.DAYS.between(startDate, endDate);
                return BigDecimal.valueOf(days)
                                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        }

        private List<String> extractCodes(List<EligibleBenefitComponentDTO> list) {
                if (list == null)
                        return Collections.emptyList();

                return list.stream()
                                .map(EligibleBenefitComponentDTO::getCode)
                                .filter(Objects::nonNull)
                                .toList();
        }

        public ApiResponseDTO<BigDecimal> getTotalAccumulationAmount(String nppfNumber) {
                MemberContributionSummary contributionSummary = memberContributionService
                                .getContributionSummary(nppfNumber);
                BigDecimal totalAccumulationAmount = contributionSummary.getTotalBalance();
                return ApiResponseDTO.<BigDecimal>builder()
                                .data(totalAccumulationAmount)
                                .build();
        }

        private List<RuleTypeResponseDto> checkEligibleRules(Long claimTypeId){
                List<ClaimTypeRuleMap> mappings = claimTypeRuleMapRepository.findByClaimTypeId(claimTypeId);
                if (!mappings.isEmpty()){
                        List<RuleTypeMaster> ruleTypes = mappings.stream()
                                .map(ClaimTypeRuleMap::getRuleType)
                                .toList(); 
                        return ruleTypeMapper.toResponseDtoList(ruleTypes);
                }
                return Collections.emptyList();
        }
}
