package com.claim.claim_processing.common.service.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.SchemeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeResponseDto;
import com.claim.claim_processing.common.DTO.update.contribution.SchemeUpdateRequestDto;

import java.util.List;

public interface SchemeService {

    List<SchemeResponseDto> getAllActive();

    SchemeResponseDto getById(Long id);

    SchemeResponseDto create(SchemeCreateRequestDto requestDto);

    SchemeResponseDto update(Long id, SchemeUpdateRequestDto requestDto);

    void deactivate(Long id);
}
