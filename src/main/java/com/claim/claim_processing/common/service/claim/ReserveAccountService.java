package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;

import java.util.List;

public interface ReserveAccountService {

    ReserveAccountResponseDto create(ReserveAccountRequestDto dto);

    ReserveAccountResponseDto update(Long id, ReserveAccountRequestDto dto);

    ReserveAccountResponseDto getById(Long id);

    List<ReserveAccountResponseDto> getAll();

    void delete(Long id);

    List<ReserveAccountResponseDto> getByAccountTypeId(Long accountTypeId);

    List<ReserveAccountResponseDto> getBySchemeTypeId(Long schemeTypeId);
}