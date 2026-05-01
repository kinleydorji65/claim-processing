package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.InterestMethodRequestDto;
import com.claim.claim_processing.common.DTO.response.common.InterestMethodResponseDto;

import java.util.List;

public interface InterestMethodService {

    InterestMethodResponseDto create(InterestMethodRequestDto dto);

    InterestMethodResponseDto update(Long id, InterestMethodRequestDto dto);

    InterestMethodResponseDto getById(Long id);

    InterestMethodResponseDto getByCode(String code);

    List<InterestMethodResponseDto> getAll();

    List<InterestMethodResponseDto> getAllActive();

    void delete(Long id);
}