package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.SubmissionChannelRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.SubmissionChannelResponseDto;
import com.claim.claim_processing.common.DTO.update.common.SubmissionChannelUpdateDto;

import java.util.List;

public interface SubmissionChannelService {

    ApiResponseDTO<SubmissionChannelResponseDto> create(
            SubmissionChannelRequestDto requestDto
    );

    ApiResponseDTO<SubmissionChannelResponseDto> update(
            Long id,
            SubmissionChannelUpdateDto updateDto
    );

    ApiResponseDTO<SubmissionChannelResponseDto> getById(
            Long id
    );

    ApiResponseDTO<SubmissionChannelResponseDto> getByCode(
            String code
    );

    ApiResponseDTO<List<SubmissionChannelResponseDto>> getAll();

    ApiResponseDTO<List<SubmissionChannelResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(
            Long id
    );
}