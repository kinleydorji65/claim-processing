package com.claim.claim_processing.common.service.wrongRemittance;

import com.claim.claim_processing.common.DTO.request.wrongRemittance.RemittanceReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.wrongRemittance.RemittanceReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.wrongRemittance.RemittanceReasonUpdateDto;

import java.util.List;

public interface RemittanceReasonService {

    RemittanceReasonResponseDto create(RemittanceReasonRequestDto requestDto);

    RemittanceReasonResponseDto getById(Long id);

    RemittanceReasonResponseDto getByCode(String code);

    List<RemittanceReasonResponseDto> getAll();

    List<RemittanceReasonResponseDto> getAllActive();

    RemittanceReasonResponseDto update(Long id, RemittanceReasonUpdateDto updateDto);

    void deactivate(Long id);
}