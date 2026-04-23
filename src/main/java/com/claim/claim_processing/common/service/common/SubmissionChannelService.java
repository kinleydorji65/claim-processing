package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.SubmissionChannelRequestDto;
import com.claim.claim_processing.common.DTO.response.common.SubmissionChannelResponseDto;
import com.claim.claim_processing.common.DTO.update.common.SubmissionChannelUpdateDto;

import java.util.List;

public interface SubmissionChannelService {

    SubmissionChannelResponseDto create(SubmissionChannelRequestDto requestDto);

    SubmissionChannelResponseDto getById(Long id);

    SubmissionChannelResponseDto getByCode(String code);

    List<SubmissionChannelResponseDto> getAll();

    List<SubmissionChannelResponseDto> getAllActive();

    SubmissionChannelResponseDto update(Long id, SubmissionChannelUpdateDto updateDto);

    void deactivate(Long id);
}