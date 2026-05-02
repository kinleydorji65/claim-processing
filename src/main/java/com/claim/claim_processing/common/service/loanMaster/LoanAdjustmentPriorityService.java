package com.claim.claim_processing.common.service.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanAdjustmentPriorityRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanAdjustmentPriorityResponseDto;

import java.util.List;

public interface LoanAdjustmentPriorityService {

    LoanAdjustmentPriorityResponseDto create(
            LoanAdjustmentPriorityRequestDto dto
    );

    LoanAdjustmentPriorityResponseDto update(
            Long id,
            LoanAdjustmentPriorityRequestDto dto
    );

    LoanAdjustmentPriorityResponseDto getById(Long id);

    List<LoanAdjustmentPriorityResponseDto> getAll();

    List<LoanAdjustmentPriorityResponseDto> getAllActive();

    // FK lookup
    List<LoanAdjustmentPriorityResponseDto> getByLoanTypeId(Long loanTypeId);

    void delete(Long id);
}