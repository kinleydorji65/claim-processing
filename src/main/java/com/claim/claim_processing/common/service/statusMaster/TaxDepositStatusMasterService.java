package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.TaxDepositStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.TaxDepositStatusResponseDto;

import java.util.List;

public interface TaxDepositStatusMasterService {

    ApiResponseDTO<TaxDepositStatusResponseDto> create(TaxDepositStatusRequestDto dto);

    ApiResponseDTO<TaxDepositStatusResponseDto> update(Long id, TaxDepositStatusRequestDto dto);

    ApiResponseDTO<TaxDepositStatusResponseDto> getById(Long id);

    ApiResponseDTO<TaxDepositStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<TaxDepositStatusResponseDto>> getAll();

    ApiResponseDTO<List<TaxDepositStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}