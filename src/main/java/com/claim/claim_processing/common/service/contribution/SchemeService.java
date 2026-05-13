package com.claim.claim_processing.common.service.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.SchemeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.contribution.SchemeUpdateRequestDto;

import java.util.List;

public interface SchemeService {

    ApiResponseDTO<SchemeTypeResponseDto> create(
            SchemeCreateRequestDto dto
    );

    ApiResponseDTO<SchemeTypeResponseDto> update(
            Long id,
            SchemeUpdateRequestDto dto
    );

    ApiResponseDTO<SchemeTypeResponseDto> getById(
            Long id
    );

    ApiResponseDTO<List<SchemeTypeResponseDto>> getAll();

    ApiResponseDTO<List<SchemeTypeResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(
            Long id
    );
}