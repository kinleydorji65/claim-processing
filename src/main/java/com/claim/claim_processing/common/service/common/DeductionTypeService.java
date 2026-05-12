package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.DeductionTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.DeductionTypeResponseDto;

import java.util.List;

public interface DeductionTypeService {

    ApiResponseDTO<DeductionTypeResponseDto> create(DeductionTypeRequestDto dto);

    ApiResponseDTO<DeductionTypeResponseDto> getById(Long id);

    ApiResponseDTO<DeductionTypeResponseDto> getByCode(String code);

    ApiResponseDTO<List<DeductionTypeResponseDto>> getAllActive();

    ApiResponseDTO<DeductionTypeResponseDto> patch(Long id, DeductionTypeRequestDto dto);

    ApiResponseDTO<String> delete(Long id);
}