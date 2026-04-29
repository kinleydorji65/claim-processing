package com.claim.claim_processing.common.service.calculationMaster;

import com.claim.claim_processing.common.DTO.request.calculationMaster.CalculationTriggerTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.calculationMaster.CalculationTriggerTypeResponseDto;

import java.util.List;

public interface CalculationTriggerTypeService {

    CalculationTriggerTypeResponseDto create(CalculationTriggerTypeRequestDto dto);

    CalculationTriggerTypeResponseDto patch(CalculationTriggerTypeRequestDto dto);

    CalculationTriggerTypeResponseDto getById(Long id);

    List<CalculationTriggerTypeResponseDto> getAll();

    List<CalculationTriggerTypeResponseDto> getAllActive();

    void delete(Long id); // soft delete recommended
}