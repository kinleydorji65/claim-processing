package com.claim.claim_processing.common.service.wrongRemittance;

import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.wrongRemittance.RemittanceReasonUpdateDto;

import java.util.List;

public interface RemittanceReasonService {

    ApiResponseDTO<RemittanceReasonResponseDto> create(RemittanceReasonRequestDto requestDto);

    ApiResponseDTO<RemittanceReasonResponseDto> getById(Long id);

    ApiResponseDTO<RemittanceReasonResponseDto> getByCode(String code);

    ApiResponseDTO<List<RemittanceReasonResponseDto>> getAll();

    ApiResponseDTO<List<RemittanceReasonResponseDto>> getAllActive();

    ApiResponseDTO<RemittanceReasonResponseDto> update(Long id, RemittanceReasonUpdateDto updateDto);

    ApiResponseDTO<String> deactivate(Long id);
}