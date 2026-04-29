package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ClaimLapsedRefundService {

    // -------------------------------
    // CRUD
    // -------------------------------
    ClaimLapsedRefundResponseDto create(ClaimLapsedRefundRequestDto dto);

    ClaimLapsedRefundResponseDto getById(Long id);

    List<ClaimLapsedRefundResponseDto> getAll();

    ClaimLapsedRefundResponseDto update(Long id, ClaimLapsedRefundRequestDto dto);

    void delete(Long id);

    // -------------------------------
    // RULE ENGINE METHODS
    // -------------------------------
    List<ClaimLapsedRefundResponseDto> getActiveRules();

    List<ClaimLapsedRefundResponseDto> getValidRulesByDate(LocalDate date);

    ClaimLapsedRefundResponseDto getByRuleCode(String ruleCode);

    // -------------------------------
    // FK FILTER METHODS (ADMIN/UI)
    // -------------------------------
    List<ClaimLapsedRefundResponseDto> getByClaimCircumstance(Long claimCircumstanceId);

    List<ClaimLapsedRefundResponseDto> getByCessationType(Long cessationTypeId);

    List<ClaimLapsedRefundResponseDto> getBySchemeType(Long schemeTypeId);
}