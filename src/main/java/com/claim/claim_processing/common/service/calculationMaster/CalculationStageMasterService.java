package com.claim.claim_processing.common.service.calculationMaster;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationStageRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationStageResponseDto;

import java.util.List;

public interface CalculationStageMasterService {

    ApiResponseDTO<CalculationStageResponseDto> create(CalculationStageRequestDto request);

    ApiResponseDTO<CalculationStageResponseDto> update(Long id, CalculationStageRequestDto request);

    ApiResponseDTO<CalculationStageResponseDto> getById(Long id);

    ApiResponseDTO<CalculationStageResponseDto> getByCode(String code);

    ApiResponseDTO<List<CalculationStageResponseDto>> getAll();

    ApiResponseDTO<List<CalculationStageResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}