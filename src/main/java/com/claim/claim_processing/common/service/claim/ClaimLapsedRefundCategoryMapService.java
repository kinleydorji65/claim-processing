package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundCategoryMapRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundCategoryMapResponseDto;

import java.util.List;

public interface ClaimLapsedRefundCategoryMapService {

    ClaimLapsedRefundCategoryMapResponseDto create(
            ClaimLapsedRefundCategoryMapRequestDto requestDto
    );

    ClaimLapsedRefundCategoryMapResponseDto update(
            Long id,
            ClaimLapsedRefundCategoryMapRequestDto requestDto
    );

    ClaimLapsedRefundCategoryMapResponseDto getById(Long id);

    List<ClaimLapsedRefundCategoryMapResponseDto> getByRuleId(Long ruleId);

    List<ClaimLapsedRefundCategoryMapResponseDto> getByCategoryId(String categoryId);

    void delete(Long id);
}