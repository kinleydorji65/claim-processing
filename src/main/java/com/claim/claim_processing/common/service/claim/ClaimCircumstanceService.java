package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimCircumstanceCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimCircumstanceUpdateRequestDto;

import java.util.List;

public interface ClaimCircumstanceService {

    List<ClaimCircumstanceResponseDto> getAllActive();

    ClaimCircumstanceResponseDto getById(Long id);

    ClaimCircumstanceResponseDto create(ClaimCircumstanceCreateRequestDto requestDto);

    ClaimCircumstanceResponseDto update(Long id, ClaimCircumstanceUpdateRequestDto requestDto);

    void deactivate(Long id);
}
