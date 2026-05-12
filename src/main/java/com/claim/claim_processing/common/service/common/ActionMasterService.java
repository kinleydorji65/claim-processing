package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.ActionRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.ActionResponseDto;

import java.util.List;

public interface ActionMasterService {

    ApiResponseDTO<ActionResponseDto> create(ActionRequestDto dto);

    ApiResponseDTO<ActionResponseDto> patch(ActionRequestDto dto);

    ApiResponseDTO<ActionResponseDto> getById(Long id);

    ApiResponseDTO<List<ActionResponseDto>> getAll();

    ApiResponseDTO<List<ActionResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}