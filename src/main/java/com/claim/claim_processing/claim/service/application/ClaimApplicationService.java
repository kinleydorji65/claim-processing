package com.claim.claim_processing.claim.service.application;

import com.claim.claim_processing.claim.DTO.request.application.ClaimApplicationRequestDto;
import com.claim.claim_processing.claim.DTO.response.application.ClaimApplicationResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

import java.util.List;

public interface ClaimApplicationService {

    ApiResponseDTO<ClaimApplicationResponseDto> create(ClaimApplicationRequestDto request);

    ApiResponseDTO<ClaimApplicationResponseDto> update(Long id, ClaimApplicationRequestDto request);

    ApiResponseDTO<ClaimApplicationResponseDto> getById(Long id);

    ApiResponseDTO<ClaimApplicationResponseDto> getByApplicationNumber(String applicationNumber);

    ApiResponseDTO<List<ClaimApplicationResponseDto>> getAll();

    ApiResponseDTO<List<ClaimApplicationResponseDto>> getByMemberCode(String memberCode);

    ApiResponseDTO<List<ClaimApplicationResponseDto>> getByNppfNumber(String nppfNumber);
}