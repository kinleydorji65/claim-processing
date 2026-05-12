package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.ClaimSourceRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.ClaimSourceResponseDto;
import com.claim.claim_processing.common.DTO.update.common.ClaimSourceUpdateDto;

import java.util.List;

public interface ClaimSourceService {

    ApiResponseDTO<List<ClaimSourceResponseDto>> getAllActive();

    ApiResponseDTO<ClaimSourceResponseDto> getById(Long id);

    ApiResponseDTO<ClaimSourceResponseDto> getByCode(String code);

    ApiResponseDTO<ClaimSourceResponseDto> create(ClaimSourceRequestDto requestDto);

    ApiResponseDTO<ClaimSourceResponseDto> update(Long id, ClaimSourceUpdateDto updateDto);

    ApiResponseDTO<String> deactivate(Long id);

    ApiResponseDTO<String> delete(
            Long id
    );
}