package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimEligibilityUpdateRequestDto;

import java.util.List;

public interface ClaimEligibilityService {

    ApiResponseDTO<List<ClaimEligibilityResponseDto>> getAllActive();

    ApiResponseDTO<ClaimEligibilityResponseDto> getById(Long id);

    ApiResponseDTO<ClaimEligibilityResponseDto> create(ClaimEligibilityCreateRequestDto requestDto);

    ApiResponseDTO<ClaimEligibilityResponseDto> update(Long id, ClaimEligibilityUpdateRequestDto requestDto);

    ApiResponseDTO<List<ClaimEligibilityResponseDto>> getByClaimCircumstanceId(Long claimCircumstanceId);

    ApiResponseDTO<List<ClaimEligibilityResponseDto>> getBySchemeTypeId(Long schemeTypeId);

    ApiResponseDTO<List<ClaimEligibilityResponseDto>> getByRuleTypeId(Long ruleTypeId);

    ApiResponseDTO<String> deactivate(Long id);
}