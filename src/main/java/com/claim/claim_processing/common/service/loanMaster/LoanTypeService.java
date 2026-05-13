package com.claim.claim_processing.common.service.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanTypeResponseDto;

import java.util.List;

public interface LoanTypeService {

    ApiResponseDTO<LoanTypeResponseDto> create(LoanTypeRequestDto dto);

    ApiResponseDTO<LoanTypeResponseDto> update(Long id, LoanTypeRequestDto dto);

    ApiResponseDTO<LoanTypeResponseDto> getById(Long id);

    ApiResponseDTO<LoanTypeResponseDto> getByCode(String code);

    ApiResponseDTO<List<LoanTypeResponseDto>> getAll();

    ApiResponseDTO<List<LoanTypeResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}