package com.claim.claim_processing.application.service.detail;

import com.claim.claim_processing.application.DTO.request.detail.WrongRemittanceDetailRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.WrongRemittanceResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

import java.util.List;

public interface WrongRemittanceDetailService {

    ApiResponseDTO<WrongRemittanceResponseDto> create(WrongRemittanceDetailRequestDto request);

    ApiResponseDTO<WrongRemittanceResponseDto> update(Long id, WrongRemittanceDetailRequestDto request);

    ApiResponseDTO<WrongRemittanceResponseDto> getById(Long id);

    ApiResponseDTO<WrongRemittanceResponseDto> getByClaimApplicationId(Long claimApplicationId);

    ApiResponseDTO<List<WrongRemittanceResponseDto>> getByAgencyCode(String agencyCode);

    ApiResponseDTO<List<WrongRemittanceResponseDto>> getAll();

    ApiResponseDTO<Void> delete(Long id);
}