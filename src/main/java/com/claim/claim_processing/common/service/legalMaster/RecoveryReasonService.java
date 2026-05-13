package com.claim.claim_processing.common.service.legalMaster;

import com.claim.claim_processing.common.DTO.request.legalMaster.RecoveryReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.legalMaster.RecoveryReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.legalMaster.RecoveryReasonUpdateDto;

import java.util.List;

public interface RecoveryReasonService {

    ApiResponseDTO<RecoveryReasonResponseDto> create(RecoveryReasonRequestDto dto);

    ApiResponseDTO<RecoveryReasonResponseDto> update(Long id, RecoveryReasonUpdateDto dto);

    ApiResponseDTO<RecoveryReasonResponseDto> getById(Long id);

    ApiResponseDTO<RecoveryReasonResponseDto> getByCode(String code);

    ApiResponseDTO<List<RecoveryReasonResponseDto>> getAll();

    ApiResponseDTO<List<RecoveryReasonResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}