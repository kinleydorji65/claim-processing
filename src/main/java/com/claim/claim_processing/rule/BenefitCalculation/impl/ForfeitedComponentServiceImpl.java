package com.claim.claim_processing.rule.BenefitCalculation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.others.member.MemberDetailResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import com.claim.claim_processing.common.repository.claim.ClaimTypeRuleMapRepository;
import com.claim.claim_processing.exceptions.ClaimException;
import com.claim.claim_processing.integration.contribution.dto.MemberContributionSummary;
import com.claim.claim_processing.integration.contribution.service.MemberContributionService;
import com.claim.claim_processing.integration.member.service.MemberService;
import com.claim.claim_processing.rule.BenefitCalculation.ForfeitedComponentService;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.dto.ForfeitedComponentResult;
import com.claim.claim_processing.rule.ruleProcessing.dto.MatchedSubClaimRuleDto;
import com.claim.claim_processing.rule.ruleProcessing.service.RuleService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ForfeitedComponentServiceImpl implements ForfeitedComponentService {

    private final RuleService ruleService;
    private final MemberContributionService memberContributionService;
    private final MemberService memberService;
    private final ClaimTypeRuleMapRepository claimTypeRuleMapRepository;

    @Override
    public ForfeitedComponentResult processForfeitedAndVestingComponents(
            ClaimInitialPreviewRequest request) {
        
        log.info("Processing forfeited and vesting components for member: {}", request.getNppfNumber());
        
        // ============================================================
        // STEP 1: GET RULES
        // ============================================================
        List<MatchedSubClaimRuleDto> matchedRules = getMatchedRules(request);
        if (matchedRules == null || matchedRules.isEmpty()) {
            log.warn("No matched rules found for member: {}", request.getNppfNumber());
            return null;
        }

        // ============================================================
        // STEP 2: GET MEMBER DETAIL AND CONTRIBUTION SUMMARY
        // ============================================================
        MemberDetailResponseDto memberDetail = getMemberDetail(request.getNppfNumber());
        MemberContributionSummary contributionSummary = memberContributionService
                .getContributionSummary(memberDetail, request.getCessationDate());

        if (contributionSummary == null) {
            log.warn("No contribution summary found for member: {}", request.getNppfNumber());
            return null;
        }

        // ============================================================
        // STEP 3: GET RULE TYPE IDS
        // ============================================================
        List<Long> ruleTypeIds = getRuleTypeIds(request.getClaimTypeId());
        log.info("Rule type IDs found: {}", ruleTypeIds);

        // ============================================================
        // STEP 4: BUILD RESULT
        // ============================================================
        ForfeitedComponentResult result = ForfeitedComponentResult.builder()
                .forfeitedComponentCodes(new ArrayList<>())
                .loanNote("You have loan")
                .build();

        // ============================================================
        // STEP 5: PROCESS RULES - Get ONLY forfeited component codes
        // ============================================================
        for (MatchedSubClaimRuleDto matchedRule : matchedRules) {
            if (matchedRule == null) {
                continue;
            }

            String ruleCode = safeUpper(matchedRule.getRuleCode());

            // 5.2 Process Lapsed Rule - Get FORFEITED COMPONENT CODES ONLY
            if (isLapsedRule(ruleCode)) {
                List<String> forfeitedCodes = getForfeitedComponentCodes(
                        matchedRule,
                        contributionSummary);

                if (forfeitedCodes != null && !forfeitedCodes.isEmpty()) {
                    result.getForfeitedComponentCodes().addAll(forfeitedCodes);
                    log.info("Forfeited component codes found: {}", forfeitedCodes);
                }
                continue;
            }
        }
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

    /**
     * Get rule type IDs from ClaimTypeRuleMap
     */
    private List<Long> getRuleTypeIds(Long claimTypeId) {
        try {
            List<ClaimTypeRuleMap> claimRuleMaps = claimTypeRuleMapRepository
                    .findByClaimTypeId(claimTypeId);
            
            if (claimRuleMaps == null || claimRuleMaps.isEmpty()) {
                return Collections.emptyList();
            }

            return claimRuleMaps.stream()
                    .filter(Objects::nonNull)
                    .filter(map -> map.getRuleType() != null)
                    .map(map -> map.getRuleType().getId())
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching rule type IDs for claimTypeId: {}", claimTypeId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get ONLY forfeited component codes (NO amounts, NO ComponentBalanceDTO)
     */
    private List<String> getForfeitedComponentCodes(
            MatchedSubClaimRuleDto matchedRule,
            MemberContributionSummary contributionSummary) {

        List<String> componentCodes = extractComponentCodesFromMapping(matchedRule);
        
        if (componentCodes == null || componentCodes.isEmpty()) {
            return Collections.emptyList();
        }

        // Return ONLY the component codes
        return componentCodes.stream()
                .filter(Objects::nonNull)
                .map(code -> code.trim().toUpperCase())
                .distinct()
                .collect(Collectors.toList());
    }

    private MemberDetailResponseDto getMemberDetail(String nppfNumber) {
        ApiResponseDTO<MemberDetailResponseDto> response = memberService.getMemberDetails(nppfNumber);

        if (response == null || response.getData() == null) {
            throw ClaimException.notFound(
                    "Member detail not found for nppfNumber: " + nppfNumber);
        }

        return response.getData();
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

    private String safeUpper(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    // ==================== COMPONENT EXTRACTION HELPERS ====================

    private List<String> extractComponentCodesFromMapping(MatchedSubClaimRuleDto matchedRule) {
        if (matchedRule == null || matchedRule.getComponentMapping() == null) {
            return Collections.emptyList();
        }

        var mapping = matchedRule.getComponentMapping();
        List<String> codes = new ArrayList<>();

        if (isYes(mapping.getHasPfMc())) codes.add("PF_MC");
        if (isYes(mapping.getHasPfEc())) codes.add("PF_EC");
        if (isYes(mapping.getHasPfImc())) codes.add("PF_IMC");
        if (isYes(mapping.getHasPfIec())) codes.add("PF_IEC");
        if (isYes(mapping.getHasPMc())) codes.add("P_MC");
        if (isYes(mapping.getHasPEc())) codes.add("P_EC");
        if (isYes(mapping.getHasPImc())) codes.add("P_IMC");
        if (isYes(mapping.getHasPIec())) codes.add("P_IEC");
        if (isYes(mapping.getHasGc())) codes.add("GC");
        if (isYes(mapping.getHasGic())) codes.add("GIC");
        if (isYes(mapping.getHasVc())) codes.add("VC");
        if (isYes(mapping.getHasVic())) codes.add("VIC");
        if (isYes(mapping.getHasIvc())) codes.add("IVC");
        if (isYes(mapping.getHasIgc())) codes.add("IGC");

        return codes.stream().distinct().collect(Collectors.toList());
    }

    private boolean isYes(String value) {
        return "Y".equalsIgnoreCase(value);
    }
}