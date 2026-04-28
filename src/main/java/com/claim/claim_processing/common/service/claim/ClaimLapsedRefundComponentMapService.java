package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundComponentMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundComponentMapResponseDto;

import java.util.List;

public interface ClaimLapsedRefundComponentMapService {

    ClaimLapsedRefundComponentMapResponseDto create(ClaimLapsedRefundComponentMapRequestDto dto);

    ClaimLapsedRefundComponentMapResponseDto update(ClaimLapsedRefundComponentMapRequestDto dto);

    ClaimLapsedRefundComponentMapResponseDto getById(Long id);

    List<ClaimLapsedRefundComponentMapResponseDto> getByRuleId(Long ruleId);

    List<ClaimLapsedRefundComponentMapResponseDto> getByBenefitComponentTypeId(Long benefitComponentTypeId);

    void delete(Long id);
}