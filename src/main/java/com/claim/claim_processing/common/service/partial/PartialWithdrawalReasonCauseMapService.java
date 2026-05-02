package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalReasonCauseMapRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalReasonCauseMapResponseDto;

import java.util.List;

public interface PartialWithdrawalReasonCauseMapService {

    PartialWithdrawalReasonCauseMapResponseDto create(PartialWithdrawalReasonCauseMapRequestDto dto);

    PartialWithdrawalReasonCauseMapResponseDto update(Long id, PartialWithdrawalReasonCauseMapRequestDto dto);

    PartialWithdrawalReasonCauseMapResponseDto getById(Long id);

    List<PartialWithdrawalReasonCauseMapResponseDto> getAll();

    List<PartialWithdrawalReasonCauseMapResponseDto> getByReasonId(Long reasonId);

    List<PartialWithdrawalReasonCauseMapResponseDto> getByCauseId(Long causeId);

    void delete(Long id);
}