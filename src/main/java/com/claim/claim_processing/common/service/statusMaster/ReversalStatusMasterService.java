package com.claim.claim_processing.common.service.statusMaster;

import com.claim.claim_processing.common.DTO.request.statusMaster.ReversalStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.statusMaster.ReversalStatusResponseDto;

import java.util.List;

public interface ReversalStatusMasterService {

    ApiResponseDTO<ReversalStatusResponseDto> create(ReversalStatusRequestDto dto);

    ApiResponseDTO<ReversalStatusResponseDto> update(Long id, ReversalStatusRequestDto dto);

    ApiResponseDTO<ReversalStatusResponseDto> getById(Long id);

    ApiResponseDTO<ReversalStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<ReversalStatusResponseDto>> getAll();

    ApiResponseDTO<List<ReversalStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}