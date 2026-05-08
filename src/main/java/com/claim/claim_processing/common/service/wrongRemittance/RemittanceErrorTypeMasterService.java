package com.claim.claim_processing.common.service.wrongRemittance;


import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceErrorTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceErrorTypeResponseDto;

import java.util.List;

public interface RemittanceErrorTypeMasterService {

    RemittanceErrorTypeResponseDto create(RemittanceErrorTypeRequestDto dto);

    RemittanceErrorTypeResponseDto update(Long id, RemittanceErrorTypeRequestDto dto);

    RemittanceErrorTypeResponseDto getById(Long id);

    RemittanceErrorTypeResponseDto getByCode(String code);

    List<RemittanceErrorTypeResponseDto> getAll();

    List<RemittanceErrorTypeResponseDto> getAllActive();

    void delete(Long id);
}