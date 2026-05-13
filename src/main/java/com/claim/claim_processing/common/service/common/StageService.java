package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.StageRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.StageResponseDto;

import java.util.List;

public interface StageService {

    ApiResponseDTO<StageResponseDto> create(
            StageRequestDto dto
    );

    ApiResponseDTO<StageResponseDto> update(
            Long id,
            StageRequestDto dto
    );


    ApiResponseDTO<StageResponseDto> getById(
            Long id
    );


    ApiResponseDTO<StageResponseDto> getByCode(
            String code
    );


    ApiResponseDTO<List<StageResponseDto>> getAll();

    ApiResponseDTO<List<StageResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(
            Long id
    );
}