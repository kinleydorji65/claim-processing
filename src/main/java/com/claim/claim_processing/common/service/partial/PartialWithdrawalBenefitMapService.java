package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalBenefitMapRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalBenefitMapResponseDto;

import java.util.List;

public interface PartialWithdrawalBenefitMapService {

    PartialWithdrawalBenefitMapResponseDto create(PartialWithdrawalBenefitMapRequestDto dto);

    PartialWithdrawalBenefitMapResponseDto update(PartialWithdrawalBenefitMapRequestDto dto);

    PartialWithdrawalBenefitMapResponseDto getById(Long id);

    List<PartialWithdrawalBenefitMapResponseDto> getByAccumulationId(Long accumulationId);

    List<PartialWithdrawalBenefitMapResponseDto> getByBenefitComponentId(Long benefitComponentId);

    void delete(Long id);
}