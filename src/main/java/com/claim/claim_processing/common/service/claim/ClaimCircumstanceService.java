package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimCircumstanceCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimCircumstanceResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.ClaimCircumstanceUpdateRequestDto;

import java.util.List;

public interface ClaimCircumstanceService {

    ApiResponseDTO<List<ClaimCircumstanceResponseDto>> getAllActive();

    ApiResponseDTO<ClaimCircumstanceResponseDto> getById(Long id);

    ApiResponseDTO<List<ClaimCircumstanceResponseDto>> getAll();

    ApiResponseDTO<ClaimCircumstanceResponseDto> getByCode(
            String code
    );

    ApiResponseDTO<ClaimCircumstanceResponseDto> create(ClaimCircumstanceCreateRequestDto requestDto);

    ApiResponseDTO<ClaimCircumstanceResponseDto> update(Long id, ClaimCircumstanceUpdateRequestDto requestDto);

    ApiResponseDTO<String> delete(
            Long id
    );
}
