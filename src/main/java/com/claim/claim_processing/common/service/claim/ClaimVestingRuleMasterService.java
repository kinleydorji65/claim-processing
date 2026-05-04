package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleResponseDto;

import java.util.List;

public interface ClaimVestingRuleMasterService {

    ClaimVestingRuleResponseDto createRule(ClaimVestingRuleRequestDto requestDto);

    ClaimVestingRuleResponseDto updateRule(Long id, ClaimVestingRuleRequestDto requestDto);

    ClaimVestingRuleResponseDto getById(Long id);

    List<ClaimVestingRuleResponseDto> getAll();

    List<ClaimVestingRuleResponseDto> getByCategoryId(String categoryId);

    List<ClaimVestingRuleResponseDto> getByRefundId(Long refundId);

    List<ClaimVestingRuleResponseDto> getByRuleTypeId(Long ruleTypeId);

    List<ClaimVestingRuleResponseDto> getByCutoffId(Long cutoffId);

    void deleteRule(Long id);
}