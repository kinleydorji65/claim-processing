package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialWithdrawalReasonUpdateDto;

import java.util.List;

public interface PartialReasonService {

    PartialWithdrawalReasonResponseDto create(PartialWithdrawalReasonRequestDto requestDto);

    PartialWithdrawalReasonResponseDto getById(Long id);

    PartialWithdrawalReasonResponseDto getByCode(String code);

    List<PartialWithdrawalReasonResponseDto> getAll();

    List<PartialWithdrawalReasonResponseDto> getAllActive();

    PartialWithdrawalReasonResponseDto update(Long id, PartialWithdrawalReasonUpdateDto updateDto);

    void deactivate(Long id);
}