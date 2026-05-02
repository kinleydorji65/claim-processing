package com.claim.claim_processing.common.service.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanStatusResponseDto;

import java.util.List;

public interface LoanStatusService {

    LoanStatusResponseDto create(LoanStatusRequestDto dto);

    LoanStatusResponseDto update(Long id, LoanStatusRequestDto dto);

    LoanStatusResponseDto getById(Long id);

    LoanStatusResponseDto getByCode(String code);

    List<LoanStatusResponseDto> getAll();

    List<LoanStatusResponseDto> getAllActive();

    void delete(Long id);
}