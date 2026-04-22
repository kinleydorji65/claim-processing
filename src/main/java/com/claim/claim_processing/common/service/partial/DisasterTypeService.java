package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.DisasterTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.DisasterTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.DisasterTypeUpdateDto;

import java.util.List;

public interface DisasterTypeService {

    DisasterTypeResponseDto create(DisasterTypeRequestDto requestDto);

    DisasterTypeResponseDto getById(Long id);

    DisasterTypeResponseDto getByCode(String code);

    List<DisasterTypeResponseDto> getAll();

    List<DisasterTypeResponseDto> getAllActive();

    DisasterTypeResponseDto update(Long id, DisasterTypeUpdateDto updateDto);

    void delete(Long id);
}