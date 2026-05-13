package com.claim.claim_processing.common.service.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanAdjustmentPriorityRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanAdjustmentPriorityResponseDto;

import java.util.List;

public interface LoanAdjustmentPriorityService {

    ApiResponseDTO<LoanAdjustmentPriorityResponseDto> create(LoanAdjustmentPriorityRequestDto dto);

    ApiResponseDTO<LoanAdjustmentPriorityResponseDto> update(Long id, LoanAdjustmentPriorityRequestDto dto);

    ApiResponseDTO<LoanAdjustmentPriorityResponseDto> getById(Long id);

    ApiResponseDTO<List<LoanAdjustmentPriorityResponseDto>> getAll();

    ApiResponseDTO<List<LoanAdjustmentPriorityResponseDto>> getAllActive();

    ApiResponseDTO<List<LoanAdjustmentPriorityResponseDto>> getByLoanTypeId(Long loanTypeId);

    ApiResponseDTO<String> delete(Long id);
}