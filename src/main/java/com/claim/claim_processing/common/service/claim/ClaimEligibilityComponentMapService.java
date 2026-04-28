package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityComponentMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityComponentMapResponseDto;

import java.util.List;

public interface ClaimEligibilityComponentMapService {

    ClaimEligibilityComponentMapResponseDto create(ClaimEligibilityComponentMapRequestDto dto);

    ClaimEligibilityComponentMapResponseDto update(ClaimEligibilityComponentMapRequestDto dto);

    ClaimEligibilityComponentMapResponseDto getById(Long id);

    List<ClaimEligibilityComponentMapResponseDto> getAll();

    List<ClaimEligibilityComponentMapResponseDto> getByRuleId(Long ruleId);

    void delete(Long id);
}