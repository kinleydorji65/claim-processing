package com.claim.claim_processing.common.service.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.SchemeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.contribution.SchemeUpdateRequestDto;

import java.util.List;

public interface SchemeService {

    List<SchemeTypeResponseDto> getAllActive();

    SchemeTypeResponseDto getById(Long id);

    SchemeTypeResponseDto create(SchemeCreateRequestDto requestDto);

    SchemeTypeResponseDto update(Long id, SchemeUpdateRequestDto requestDto);

    void deactivate(Long id);
}
