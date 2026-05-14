package com.claim.claim_processing.common.service.wrongRemittance;


import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceErrorTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceErrorTypeResponseDto;

import java.util.List;

public interface RemittanceErrorTypeMasterService {

    ApiResponseDTO<RemittanceErrorTypeResponseDto> create(RemittanceErrorTypeRequestDto dto);

    ApiResponseDTO<RemittanceErrorTypeResponseDto> update(Long id, RemittanceErrorTypeRequestDto dto);

    ApiResponseDTO<RemittanceErrorTypeResponseDto> getById(Long id);

    ApiResponseDTO<RemittanceErrorTypeResponseDto> getByCode(String code);

    ApiResponseDTO<List<RemittanceErrorTypeResponseDto>> getAll();

    ApiResponseDTO<List<RemittanceErrorTypeResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}