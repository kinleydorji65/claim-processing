package com.claim.claim_processing.application.service.detail;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiaryClaimantRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiaryClaimantResponseDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;

import java.util.List;

public interface BeneficiaryClaimantDetailService {

    ApiResponseDTO<BeneficiaryClaimantResponseDto> create(
            BeneficiaryClaimantRequestDto request);

    ApiResponseDTO<BeneficiaryClaimantResponseDto> update(
            Long id,
            BeneficiaryClaimantRequestDto request);

    ApiResponseDTO<BeneficiaryClaimantResponseDto> getById(
            Long id);

    ApiResponseDTO<List<BeneficiaryClaimantResponseDto>> getByBeneficiarySettlementDetailId(
            Long beneficiarySettlementDetailId);

    ApiResponseDTO<List<BeneficiaryClaimantResponseDto>> getAll();

    ApiResponseDTO<Void> delete(
            Long id);
}