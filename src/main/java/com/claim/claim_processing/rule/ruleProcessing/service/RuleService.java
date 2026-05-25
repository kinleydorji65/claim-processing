package com.claim.claim_processing.rule.ruleProcessing.service;

import java.util.List;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.rule.dto.ClaimInitialPreviewRequest;
import com.claim.claim_processing.rule.ruleGateWay.dto.MatchedConditionRuleDto;

public interface RuleService {
    ApiResponseDTO<List<MatchedConditionRuleDto>> playWithRule(ClaimInitialPreviewRequest request);
}
