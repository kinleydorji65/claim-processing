package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.ClaimTypeDeductionMapRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ClaimTypeDeductionMapResponseDto;

import java.util.List;

public interface ClaimTypeDeductionMapService {

    ClaimTypeDeductionMapResponseDto create(ClaimTypeDeductionMapRequestDto dto);

    ClaimTypeDeductionMapResponseDto update(Long id, ClaimTypeDeductionMapRequestDto dto);

    ClaimTypeDeductionMapResponseDto patch(Long id, ClaimTypeDeductionMapRequestDto dto);

    ClaimTypeDeductionMapResponseDto getById(Long id);

    List<ClaimTypeDeductionMapResponseDto> getAll();

    List<ClaimTypeDeductionMapResponseDto> getByClaimTypeId(Long claimTypeId);

    List<ClaimTypeDeductionMapResponseDto> getByDeductionTypeId(Long deductionTypeId);

    void delete(Long id);
}