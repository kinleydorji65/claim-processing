package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialCauseResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialCauseUpdateDto;

import java.util.List;

public interface PartialCauseService {

    PartialCauseResponseDto create(PartialCauseRequestDto requestDto);

    PartialCauseResponseDto getById(Long id);

    PartialCauseResponseDto getByCode(String code);

    List<PartialCauseResponseDto> getAll();

    List<PartialCauseResponseDto> getAllActive();

    PartialCauseResponseDto update(Long id, PartialCauseUpdateDto updateDto);

    void delete(Long id);
}