package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.ClaimSourceRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ClaimSourceResponseDto;
import com.claim.claim_processing.common.DTO.update.common.ClaimSourceUpdateDto;

import java.util.List;

public interface ClaimSourceService {

    ClaimSourceResponseDto create(ClaimSourceRequestDto requestDto);

    ClaimSourceResponseDto getById(Long id);

    ClaimSourceResponseDto getByCode(String code);

    List<ClaimSourceResponseDto> getAll();

    List<ClaimSourceResponseDto> getAllActive();

    ClaimSourceResponseDto update(Long id, ClaimSourceUpdateDto updateDto);

    void deactivate(Long id);
}