package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.VerificationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.VerificationStatusResponseDto;

import java.util.List;

public interface VerificationStatusMasterService {

    ApiResponseDTO<VerificationStatusResponseDto> create(VerificationStatusRequestDto dto);

    ApiResponseDTO<VerificationStatusResponseDto> update(Long id, VerificationStatusRequestDto dto);

    ApiResponseDTO<VerificationStatusResponseDto> getById(Long id);

    ApiResponseDTO<VerificationStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<VerificationStatusResponseDto>> getAll();

    ApiResponseDTO<List<VerificationStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}