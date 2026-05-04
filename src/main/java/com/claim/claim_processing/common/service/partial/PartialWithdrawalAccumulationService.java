package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalAccumulationRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalAccumulationResponseDto;

import java.util.List;

public interface PartialWithdrawalAccumulationService {

    PartialWithdrawalAccumulationResponseDto create(PartialWithdrawalAccumulationRequestDto dto);

    PartialWithdrawalAccumulationResponseDto update(Long id, PartialWithdrawalAccumulationRequestDto dto);

    PartialWithdrawalAccumulationResponseDto getById(Long id);

    List<PartialWithdrawalAccumulationResponseDto> getAll();

    List<PartialWithdrawalAccumulationResponseDto> getAllActive();

    void delete(Long id);
}