package com.claim.claim_processing.rule.ruleProcessing.service;

import java.util.List;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.dto.FinalContributionRequest;
import com.claim.claim_processing.rule.ruleGateWay.dto.MatchedSubClaimRuleDto;

public interface RuleService {
    ApiResponseDTO<List<MatchedSubClaimRuleDto>> playWithRule(
            ClaimInitialPreviewRequest request);
}
