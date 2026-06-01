package com.claim.claim_processing.application.service.detail;

import com.claim.claim_processing.application.DTO.request.detail.BeneficiarySettlementDetailRequestDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiarySettlementResponseDto;

import java.util.List;

public interface BeneficiarySettlementDetailService {

    BeneficiarySettlementResponseDto create(BeneficiarySettlementDetailRequestDto request);

    BeneficiarySettlementResponseDto patch(Long id, BeneficiarySettlementDetailRequestDto request);

    BeneficiarySettlementResponseDto getById(Long id);

    BeneficiarySettlementResponseDto getByClaimApplicationId(Long claimApplicationId);

    BeneficiarySettlementResponseDto getByDeceasedMemberCode(String deceasedMemberCode);

    List<BeneficiarySettlementResponseDto> getAll();

    void delete(Long id);
}