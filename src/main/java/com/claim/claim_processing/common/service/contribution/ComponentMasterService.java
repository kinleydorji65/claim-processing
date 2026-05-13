package com.claim.claim_processing.common.service.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.ComponentRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.contribution.ComponentResponseDto;

import java.util.List;

public interface ComponentMasterService {

    ApiResponseDTO<ComponentResponseDto> create(
            ComponentRequestDto requestDto
    );

    ApiResponseDTO<ComponentResponseDto> update(
            Long id,
            ComponentRequestDto requestDto
    );

    ApiResponseDTO<ComponentResponseDto> getById(
            Long id
    );

    ApiResponseDTO<List<ComponentResponseDto>> getAll();

    ApiResponseDTO<List<ComponentResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(
            Long id
    );
}