package com.claim.claim_processing.common.service.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanStatusResponseDto;

import java.util.List;

public interface LoanStatusService {

    ApiResponseDTO<LoanStatusResponseDto> create(LoanStatusRequestDto dto);

    ApiResponseDTO<LoanStatusResponseDto> update(Long id, LoanStatusRequestDto dto);

    ApiResponseDTO<LoanStatusResponseDto> getById(Long id);

    ApiResponseDTO<LoanStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<LoanStatusResponseDto>> getAll();

    ApiResponseDTO<List<LoanStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}