package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;

import java.util.List;

public interface PartialWithdrawalCauseService {

    PartialWithdrawalCauseResponseDto create(PartialWithdrawalCauseRequestDto dto);

    PartialWithdrawalCauseResponseDto update(Long id, PartialWithdrawalCauseRequestDto dto);

    PartialWithdrawalCauseResponseDto getById(Long id);

    List<PartialWithdrawalCauseResponseDto> getAll();

    List<PartialWithdrawalCauseResponseDto> getByReasonId(Long reasonId);

    void delete(Long id);
}