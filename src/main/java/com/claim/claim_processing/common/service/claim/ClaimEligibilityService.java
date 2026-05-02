package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimEligibilityUpdateRequestDto;

import java.util.List;

public interface ClaimEligibilityService {

    List<ClaimEligibilityResponseDto> getAllActive();

    ClaimEligibilityResponseDto getById(Long id);

    ClaimEligibilityResponseDto create(ClaimEligibilityCreateRequestDto requestDto);

    ClaimEligibilityResponseDto update(Long id, ClaimEligibilityUpdateRequestDto requestDto);

    List<ClaimEligibilityResponseDto> getByClaimCircumstanceId(Long claimCircumstanceId);

    List<ClaimEligibilityResponseDto> getBySchemeTypeId(Long schemeTypeId);

    List<ClaimEligibilityResponseDto> getByRuleTypeId(Long ruleTypeId);

    void deactivate(Long id);
}