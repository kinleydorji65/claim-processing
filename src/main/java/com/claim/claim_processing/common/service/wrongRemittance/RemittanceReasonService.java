package com.claim.claim_processing.common.service.wrongRemittance;

import com.claim.claim_processing.common.DTO.request.wrong_remittance.RemittanceReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.wrong_remittance.RemittanceReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.wrong_remittance.RemittanceReasonUpdateDto;

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