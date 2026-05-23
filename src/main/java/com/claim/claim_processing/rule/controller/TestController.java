package com.claim.claim_processing.rule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claim.claim_processing.rule.ruleGateWay.dto.RuleResponseDto;
import com.claim.claim_processing.rule.ruleGateWay.service.RuleGateWayService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final RuleGateWayService ruleGateWayService;

    @GetMapping("/{ruleId}")
    public RuleResponseDto test(@PathVariable Long ruleId) {
        return ruleGateWayService.getByTopRuleType(ruleId);
    }
}
