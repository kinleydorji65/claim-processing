package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.CessationTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.CessationTypeUpdateRequestDto;

import java.util.List;

public interface CessationTypeService {

    ApiResponseDTO<List<CessationTypeResponseDto>> getAll();

    ApiResponseDTO<List<CessationTypeResponseDto>> getActive();

    ApiResponseDTO<CessationTypeResponseDto> getById(Long id);

    ApiResponseDTO<List<CessationTypeResponseDto>> getByClaimCircumstance(Long circumstanceId);

    ApiResponseDTO<CessationTypeResponseDto> create(CessationTypeCreateRequestDto requestDto);

    ApiResponseDTO<CessationTypeResponseDto> update(Long id, CessationTypeUpdateRequestDto requestDto);

    ApiResponseDTO<String> delete(Long id);
}