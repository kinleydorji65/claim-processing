package com.claim.claim_processing.common.service.specialCase;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseAuthorityRequestDto;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseAuthorityResponseDto;
import com.claim.claim_processing.common.DTO.update.specialCase.SpecialCaseAuthorityUpdateRequestDto;

import java.util.List;

public interface SpecialCaseAuthorityService {

    SpecialCaseAuthorityResponseDto create(SpecialCaseAuthorityRequestDto requestDto);

    SpecialCaseAuthorityResponseDto getById(Long id);

    SpecialCaseAuthorityResponseDto getByCode(String code);

    List<SpecialCaseAuthorityResponseDto> getAll();

    List<SpecialCaseAuthorityResponseDto> getAllActive();

    SpecialCaseAuthorityResponseDto update(Long id, SpecialCaseAuthorityUpdateRequestDto updateRequestDto);

    void delete(Long id);
}