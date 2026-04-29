package com.claim.claim_processing.common.service.calculationMaster;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationStageRequestDto;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationStageResponseDto;

import java.util.List;

public interface CalculationStageMasterService {

    CalculationStageResponseDto create(CalculationStageRequestDto request);

    CalculationStageResponseDto update(Long id, CalculationStageRequestDto request);

    CalculationStageResponseDto getById(Long id);

    CalculationStageResponseDto getByCode(String code);

    List<CalculationStageResponseDto> getAll();

    List<CalculationStageResponseDto> getAllActive();

    void delete(Long id);
}