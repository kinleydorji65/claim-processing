package com.claim.claim_processing.common.service.legalMaster;

import com.claim.claim_processing.common.DTO.request.legalMaster.RecoveryReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.legalMaster.RecoveryReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.legalMaster.RecoveryReasonUpdateDto;

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