package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialCauseRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalCauseResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.PartialCauseUpdateDto;

import java.util.List;

public interface PartialCauseService {

    PartialWithdrawalCauseResponseDto create(PartialCauseRequestDto requestDto);

    PartialWithdrawalCauseResponseDto getById(Long id);

    PartialWithdrawalCauseResponseDto getByCode(String code);

    List<PartialWithdrawalCauseResponseDto> getAll();

    List<PartialWithdrawalCauseResponseDto> getAllActive();

    PartialWithdrawalCauseResponseDto update(Long id, PartialCauseUpdateDto updateDto);

    void delete(Long id);
}