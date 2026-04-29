package com.claim.claim_processing.rule.claim.vesting.service;

import com.claim.claim_processing.rule.claim.DTO.request.VestingRuleRequest;
import com.claim.claim_processing.rule.claim.DTO.response.VestingRuleResponseDTO;

public interface VestingRuleService {
    VestingRuleResponseDTO determineVestingEligibility(VestingRuleRequest request);
}
