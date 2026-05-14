package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.CalculationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.CalculationStatusResponseDto;

import java.util.List;

public interface CalculationStatusMasterService {

    ApiResponseDTO<CalculationStatusResponseDto> create(CalculationStatusRequestDto dto);

    ApiResponseDTO<CalculationStatusResponseDto> update(Long id, CalculationStatusRequestDto dto);

    ApiResponseDTO<CalculationStatusResponseDto> getById(Long id);

    ApiResponseDTO<CalculationStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<CalculationStatusResponseDto>> getAll();

    ApiResponseDTO<List<CalculationStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}