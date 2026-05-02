package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeRuleMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeRuleMapResponseDto;

import java.util.List;

public interface ClaimTypeRuleMapService {

    ClaimTypeRuleMapResponseDto create(ClaimTypeRuleMapRequestDto requestDto);

    ClaimTypeRuleMapResponseDto update(Long id, ClaimTypeRuleMapRequestDto requestDto);

    ClaimTypeRuleMapResponseDto getById(Long id);

    List<ClaimTypeRuleMapResponseDto> getAll();

    List<ClaimTypeRuleMapResponseDto> getByClaimTypeId(Long claimTypeId);

    List<ClaimTypeRuleMapResponseDto> getByRuleTypeId(Long ruleTypeId);

    void delete(Long id);
}