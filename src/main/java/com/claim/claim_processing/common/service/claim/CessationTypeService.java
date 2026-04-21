package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.CessationTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.CessationTypeUpdateRequestDto;

import java.util.List;

public interface CessationTypeService {

    List<CessationTypeResponseDto> getAllActive();

    CessationTypeResponseDto getById(Long id);

    CessationTypeResponseDto create(CessationTypeCreateRequestDto requestDto);

    CessationTypeResponseDto update(Long id, CessationTypeUpdateRequestDto requestDto);

    void deactivate(Long id);
}
