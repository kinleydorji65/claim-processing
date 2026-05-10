package com.claim.claim_processing.common.service.claim;
import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeMasterResponseDto;

import java.util.List;

public interface ClaimTypeMasterService {

    ApiResponseDTO<ClaimTypeMasterResponseDto> create(ClaimTypeMasterRequestDto requestDto);

    ApiResponseDTO<ClaimTypeMasterResponseDto> update(Long id, ClaimTypeMasterRequestDto requestDto);

    ApiResponseDTO<ClaimTypeMasterResponseDto> getById(Long id);

    ApiResponseDTO<ClaimTypeMasterResponseDto> getByCode(String code);

    ApiResponseDTO<List<ClaimTypeMasterResponseDto>> getAll();

    ApiResponseDTO<List<ClaimTypeMasterResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}