package com.claim.claim_processing.rule.BenefitCalculation.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.member.service.MemberService;
import com.claim.claim_processing.rule.BenefitCalculation.CheckVestingService;
import com.claim.claim_processing.rule.claim.DTO.response.VestingResultDto;
import com.claim.claim_processing.rule.dto.CheckVestingDto;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleProcessing.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CheckVestingServiceImpl implements CheckVestingService {

    private final RuleService ruleService;
    private final MemberContributionService memberContributionService;
    private final MemberService memberService;

    @Override
    public CheckVestingDto checkVestingRules(ClaimInitialPreviewRequest request) {
        
        log.info("Processing vesting rules for member: {}", request.getNppfNumber());
        
        // ============================================================
        // STEP 1: GET RULES
        // ============================================================
        List<MatchedSubClaimRuleDto> matchedRules = getMatchedRules(request);
        if (matchedRules == null || matchedRules.isEmpty()) {
            log.warn("No matched rules found for member: {}", request.getNppfNumber());
            return buildEmptyResult("No rules found.");
        }

        // ============================================================
        // STEP 2: GET MEMBER DETAIL AND CONTRIBUTION SUMMARY
        // ============================================================
        MemberDetailResponseDto memberDetail = getMemberDetail(request.getNppfNumber());
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(memberDetail, request.getCessationDate());

        if (contributionSummary == null) {
            log.warn("No contribution summary found for member: {}", request.getNppfNumber());
            return buildEmptyResult("No contribution data found.");
        }

        // ============================================================
        // STEP 3: PROCESS VESTING RULES
        // ============================================================
        VestingInfo vestingInfo = VestingInfo.builder()
                .vestingRuleFound(false)
                .refundTypeName(null)
                .totalContributionMonths(contributionSummary.getTotalContributionMonths())
                .build();

        for (MatchedSubClaimRuleDto matchedRule : matchedRules) {
            if (matchedRule == null) {
                continue;
            }

            String ruleCode = safeUpper(matchedRule.getRuleCode());

            // Process only Vesting Rule
            if (isVestingRule(ruleCode)) {
                VestingResultDto vestingResult = handleVestingRule(matchedRule);
                vestingInfo.setVestingRuleFound(true);
                
                if (vestingResult != null) {
                    if (vestingResult.getRefundTypeName() != null 
                            && !vestingResult.getRefundTypeName().isBlank()) {
                        vestingInfo.setRefundTypeName(vestingResult.getRefundTypeName());
                    }
                }
            }
        }

        // ============================================================
        // STEP 4: BUILD RESPONSE
        // ============================================================
        CheckVestingDto result = CheckVestingDto.builder()
                .vestingNote(buildVestingNote(vestingInfo))
                .recommendedRefundType(vestingInfo.getRefundTypeName())
                .vestingRuleFound(vestingInfo.isVestingRuleFound())
                .totalContributionMonths(vestingInfo.getTotalContributionMonths())
                .loanNote("you have loan.")
                .build();

        logResult(result);
        return result;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Get matched rules from RuleService
     */
    private List<MatchedSubClaimRuleDto> getMatchedRules(ClaimInitialPreviewRequest request) {
        try {
            ApiResponseDTO<List<MatchedSubClaimRuleDto>> ruleResponse = ruleService.playWithRule(request);
            return ruleResponse != null && ruleResponse.getData() != null 
                    ? ruleResponse.getData() 
                    : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching rules for member: {}", request.getNppfNumber(), e);
            return Collections.emptyList();
        }
    }

    private VestingResultDto handleVestingRule(MatchedSubClaimRuleDto matchedRule) {
        return VestingResultDto.builder()
                .lumpSumEligible(matchedRule.getRefundTypeName() != null)
                .refundTypeName(matchedRule.getRefundTypeName())
                .build();
    }

    private MemberDetailResponseDto getMemberDetail(String nppfNumber) {
        ApiResponseDTO<MemberDetailResponseDto> response = memberService.getMemberDetails(nppfNumber);

        if (response == null || response.getData() == null) {
            throw ClaimException.notFound(
                    "Member detail not found for nppfNumber: " + nppfNumber);
        }

        return response.getData();
    }

    // ==================== RULE PROCESSING HELPERS ====================

    private boolean isVestingRule(String ruleCode) {
        return ruleCode != null && ruleCode.toUpperCase().contains("VESTING");
    }

    private String safeUpper(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    // ==================== NOTE BUILDING HELPERS ====================

    private String buildVestingNote(VestingInfo vestingInfo) {
        if (!vestingInfo.isVestingRuleFound()) {
            return "No vesting rule applied.";
        }

        return String.format(
                "Till Date, Your total Contribution Months is %d. " +
                "Recommended benefit type is %s. ",
                vestingInfo.getTotalContributionMonths(),
                vestingInfo.getRefundTypeName() != null ? vestingInfo.getRefundTypeName() : "Not Determined"
        );
    }

    private CheckVestingDto buildEmptyResult(String note) {
        return CheckVestingDto.builder()
                .vestingNote(note)
                .recommendedRefundType(null)
                .vestingRuleFound(false)
                .totalContributionMonths(0)
                .build();
    }

    private void logResult(CheckVestingDto result) {
        log.info("=== FINAL RESULT ===");
        log.info("Vesting Rule Found: {}", result.isVestingRuleFound());
        log.info("Recommended Refund Type: {}", result.getRecommendedRefundType());
        log.info("Total Contribution Months: {}", result.getTotalContributionMonths());
    }

    // ==================== INNER CLASS ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class VestingInfo {
        private boolean vestingRuleFound;
        private String refundTypeName;
        private Integer totalContributionMonths;
    }
}