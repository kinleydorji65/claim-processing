package com.claim.claim_processing.common.service.calculationMaster;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationTriggerTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationTriggerTypeResponseDto;

import java.util.List;

public interface CalculationTriggerTypeService {

    ApiResponseDTO<CalculationTriggerTypeResponseDto> create(CalculationTriggerTypeRequestDto dto);

    ApiResponseDTO<CalculationTriggerTypeResponseDto> update(Long id, CalculationTriggerTypeRequestDto dto);

    ApiResponseDTO<CalculationTriggerTypeResponseDto> getById(Long id);

    ApiResponseDTO<CalculationTriggerTypeResponseDto> getByCode(String code);

    ApiResponseDTO<List<CalculationTriggerTypeResponseDto>> getAll();

    ApiResponseDTO<List<CalculationTriggerTypeResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}