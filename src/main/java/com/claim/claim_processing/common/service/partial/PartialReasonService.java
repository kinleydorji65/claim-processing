package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialReasonUpdateDto;

import java.util.List;

public interface PartialReasonService {

    PartialReasonResponseDto create(PartialReasonRequestDto requestDto);

    PartialReasonResponseDto getById(Long id);

    PartialReasonResponseDto getByCode(String code);

    List<PartialReasonResponseDto> getAll();

    List<PartialReasonResponseDto> getAllActive();

    PartialReasonResponseDto update(Long id, PartialReasonUpdateDto updateDto);

    void delete(Long id);
}