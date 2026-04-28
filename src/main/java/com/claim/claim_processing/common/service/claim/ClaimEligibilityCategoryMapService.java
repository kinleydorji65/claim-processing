package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimEligibilityCategoryMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimEligibilityCategoryMapResponseDto;

import java.util.List;

public interface ClaimEligibilityCategoryMapService {

    ClaimEligibilityCategoryMapResponseDto create(ClaimEligibilityCategoryMapRequestDto dto);

    List<ClaimEligibilityCategoryMapResponseDto> getAll();

    List<ClaimEligibilityCategoryMapResponseDto> getByCategoryId(String categoryId);

    List<ClaimEligibilityCategoryMapResponseDto> getByRuleId(Long ruleId);

    void delete(Long id);
}