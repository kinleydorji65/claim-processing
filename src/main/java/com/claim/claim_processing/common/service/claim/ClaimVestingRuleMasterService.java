package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleMasterResponseDto;

import java.util.List;

public interface ClaimVestingRuleMasterService {

    ClaimVestingRuleMasterResponseDto createRule(ClaimVestingRuleMasterRequestDto requestDto);

    ClaimVestingRuleMasterResponseDto updateRule(Long id, ClaimVestingRuleMasterRequestDto requestDto);

    ClaimVestingRuleMasterResponseDto getById(Long id);

    List<ClaimVestingRuleMasterResponseDto> getAll();

    List<ClaimVestingRuleMasterResponseDto> getByCategoryId(String categoryId);

    void deleteRule(Long id);
}