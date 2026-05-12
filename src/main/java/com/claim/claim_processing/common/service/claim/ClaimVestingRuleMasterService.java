package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleResponseDto;

import java.util.List;

public interface ClaimVestingRuleMasterService {

    ApiResponseDTO<ClaimVestingRuleResponseDto> createRule(ClaimVestingRuleRequestDto requestDto);

    ApiResponseDTO<ClaimVestingRuleResponseDto> updateRule(Long id, ClaimVestingRuleRequestDto requestDto);

    ApiResponseDTO<ClaimVestingRuleResponseDto> getById(Long id);

    ApiResponseDTO<List<ClaimVestingRuleResponseDto>> getAll();

    ApiResponseDTO<List<ClaimVestingRuleResponseDto>> getByCategoryId(String categoryId);

    ApiResponseDTO<List<ClaimVestingRuleResponseDto>> getByRefundId(Long refundId);

    ApiResponseDTO<List<ClaimVestingRuleResponseDto>> getByRuleTypeId(Long ruleTypeId);

    ApiResponseDTO<String> deleteRule(Long id);
}