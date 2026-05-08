package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.TaxDepositStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.TaxDepositStatusResponseDto;

import java.util.List;

public interface TaxDepositStatusMasterService {

    TaxDepositStatusResponseDto create(TaxDepositStatusRequestDto dto);

    TaxDepositStatusResponseDto update(Long id, TaxDepositStatusRequestDto dto);

    TaxDepositStatusResponseDto getById(Long id);

    TaxDepositStatusResponseDto getByCode(String code);

    List<TaxDepositStatusResponseDto> getAll();

    List<TaxDepositStatusResponseDto> getAllActive();

    void delete(Long id);
}