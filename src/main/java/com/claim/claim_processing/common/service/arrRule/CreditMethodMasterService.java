package com.claim.claim_processing.common.service.arrRule;

import com.claim.claim_processing.common.DTO.request.arrRule.CreditMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.arrRule.CreditMethodResponseDto;

import java.util.List;

public interface CreditMethodMasterService {

    CreditMethodResponseDto create(CreditMethodRequestDto request);

    CreditMethodResponseDto update(Long id, CreditMethodRequestDto request);

    CreditMethodResponseDto getById(Long id);

    CreditMethodResponseDto getByCode(String code);

    List<CreditMethodResponseDto> getAllActive();

    void delete(Long id);
}