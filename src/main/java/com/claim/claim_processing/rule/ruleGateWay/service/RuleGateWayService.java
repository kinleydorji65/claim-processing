package com.claim.claim_processing.rule.ruleGateWay.service;

import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto;

public interface RuleGateWayService {
    RuleResponseDto getByTopRuleType(Long ruleId);
}
