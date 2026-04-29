package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.ActionRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ActionResponseDto;

import java.util.List;

public interface ActionMasterService {

    ActionResponseDto create(ActionRequestDto dto);

    ActionResponseDto patch(ActionRequestDto dto);

    ActionResponseDto getById(Long id);

    List<ActionResponseDto> getAll();

    List<ActionResponseDto> getAllActive();

    void delete(Long id);
}