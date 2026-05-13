package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.ReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.ReviewStatusResponseDto;

import java.util.List;

public interface ReviewStatusService {

    ApiResponseDTO<ReviewStatusResponseDto> create(ReviewStatusRequestDto dto);

    ApiResponseDTO<ReviewStatusResponseDto> update(Long id, ReviewStatusRequestDto dto);

    ApiResponseDTO<ReviewStatusResponseDto> getById(Long id);

    ApiResponseDTO<ReviewStatusResponseDto> getByCode(String code);

    ApiResponseDTO<List<ReviewStatusResponseDto>> getAll();

    ApiResponseDTO<List<ReviewStatusResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}