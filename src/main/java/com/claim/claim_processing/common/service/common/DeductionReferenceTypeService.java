package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.DeductionReferenceTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.DeductionReferenceTypeResponseDto;

import java.util.List;

public interface DeductionReferenceTypeService {

    ApiResponseDTO<DeductionReferenceTypeResponseDto> create(DeductionReferenceTypeRequestDto dto);

    ApiResponseDTO<List<DeductionReferenceTypeResponseDto>> getAll();

    ApiResponseDTO<List<DeductionReferenceTypeResponseDto>> getAllActive();

    ApiResponseDTO<DeductionReferenceTypeResponseDto> getById(Long id);

    ApiResponseDTO<DeductionReferenceTypeResponseDto> getByCode(String code);

    ApiResponseDTO<DeductionReferenceTypeResponseDto> update(Long id, DeductionReferenceTypeRequestDto dto);

    ApiResponseDTO<String> delete(Long id);
}