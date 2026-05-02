package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.ReviewStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.common.ReviewStatusResponseDto;

import java.util.List;

public interface ReviewStatusService {

    ReviewStatusResponseDto create(ReviewStatusRequestDto dto);

    ReviewStatusResponseDto update(Long id, ReviewStatusRequestDto dto);

    ReviewStatusResponseDto getById(Long id);

    ReviewStatusResponseDto getByCode(String code);

    List<ReviewStatusResponseDto> getAll();

    List<ReviewStatusResponseDto> getAllActive();

    void delete(Long id);
}