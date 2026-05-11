package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;

import java.util.List;

public interface ClaimLapsedRefundService {

    // -------------------------------
    // CRUD
    // -------------------------------
    ApiResponseDTO<ClaimLapsedRefundResponseDto> create(ClaimLapsedRefundRequestDto dto);

    ApiResponseDTO<ClaimLapsedRefundResponseDto> getById(Long id);

    ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> getAll();

    ApiResponseDTO<ClaimLapsedRefundResponseDto> update(Long id, ClaimLapsedRefundRequestDto dto);

    ApiResponseDTO<String> delete(Long id);

    // -------------------------------
    // RULE ENGINE METHODS
    // -------------------------------
    ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> getActiveRules();

    ApiResponseDTO<ClaimLapsedRefundResponseDto> getByRuleCode(String ruleCode);

    // -------------------------------
    // FK FILTER METHODS (ADMIN/UI)
    // -------------------------------
    ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> getByClaimCircumstance(Long claimCircumstanceId);
    ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> getBySchemeType(Long schemeTypeId);
    ApiResponseDTO<List<ClaimLapsedRefundResponseDto>> getByRuleType(Long ruleTypeId);
}