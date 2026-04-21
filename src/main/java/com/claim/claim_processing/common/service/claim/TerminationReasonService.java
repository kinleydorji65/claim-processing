package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.TerminationReasonCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.TerminationReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.TerminationReasonUpdateRequestDto;

import java.util.List;

public interface TerminationReasonService {

    List<TerminationReasonResponseDto> getAllActive();

    TerminationReasonResponseDto getById(Long id);

    TerminationReasonResponseDto create(TerminationReasonCreateRequestDto requestDto);

    TerminationReasonResponseDto update(Long id, TerminationReasonUpdateRequestDto requestDto);

    void deactivate(Long id);
}