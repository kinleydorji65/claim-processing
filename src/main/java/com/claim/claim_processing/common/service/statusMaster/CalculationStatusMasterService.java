package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.CalculationStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.CalculationStatusResponseDto;

import java.util.List;

public interface CalculationStatusMasterService {

    CalculationStatusResponseDto create(CalculationStatusRequestDto dto);

    CalculationStatusResponseDto update(Long id, CalculationStatusRequestDto dto);

    CalculationStatusResponseDto getById(Long id);

    CalculationStatusResponseDto getByCode(String code);

    List<CalculationStatusResponseDto> getAll();

    List<CalculationStatusResponseDto> getAllActive();

    void delete(Long id);
}