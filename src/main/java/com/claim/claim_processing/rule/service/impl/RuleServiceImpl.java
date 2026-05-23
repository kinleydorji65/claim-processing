package com.claim.claim_processing.rule.service.impl;

import org.springframework.stereotype.Service;

import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto;
import com.claim.claim_processing.rule.ruleGateWay.service.RuleGateWayService;
import com.claim.claim_processing.rule.service.RuleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {
    private final RuleGateWayService ruleGateWayService;

    private void playWithRule(){
        RuleResponseDto ruleResponseDto = ruleGateWayService.getByTopRuleType(1L);
    }
}
