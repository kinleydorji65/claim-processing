package com.claim.claim_processing.common.service.legalMaster;

import com.claim.claim_processing.common.DTO.request.legal_master.RecoveryReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.legal_master.RecoveryReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.legal_master.RecoveryReasonUpdateDto;

import java.util.List;

public interface RecoveryReasonService {

    RecoveryReasonResponseDto create(RecoveryReasonRequestDto requestDto);

    RecoveryReasonResponseDto getById(Long id);

    RecoveryReasonResponseDto getByCode(String code);

    List<RecoveryReasonResponseDto> getAll();

    List<RecoveryReasonResponseDto> getAllActive();

    RecoveryReasonResponseDto update(Long id, RecoveryReasonUpdateDto updateDto);

    void delete(Long id);
}