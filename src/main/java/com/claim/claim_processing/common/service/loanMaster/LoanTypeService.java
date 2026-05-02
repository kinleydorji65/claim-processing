package com.claim.claim_processing.common.service.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanTypeResponseDto;

import java.util.List;

public interface LoanTypeService {

    LoanTypeResponseDto create(LoanTypeRequestDto dto);

    LoanTypeResponseDto update(Long id, LoanTypeRequestDto dto);

    LoanTypeResponseDto getById(Long id);

    LoanTypeResponseDto getByCode(String code);

    List<LoanTypeResponseDto> getAll();

    List<LoanTypeResponseDto> getAllActive();

    void delete(Long id);
}