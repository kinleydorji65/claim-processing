package com.claim.claim_processing.rule.claim.DTO.response;

import java.util.List;

import com.claim.claim_processing.rule.claim.DTO.contribution.EligibleBenefitComponentDTO;
import com.claim.claim_processing.rule.claim.DTO.contribution.MemberContributionSummary;
import com.claim.claim_processing.rule.claim.DTO.result.RuleEvaluationResultDto;

import lombok.Data;

@Data
public class ClaimEligibilityPreviewResponse {
    private boolean eligible;
    private String matchingRuleCode;
    private String matchingRuleName;
    private MemberContributionSummary contributionSummary;
    private List<EligibleBenefitComponentDTO> eligibleBenefits;
    private List<RuleEvaluationResultDto> ruleEvaluations;
}
