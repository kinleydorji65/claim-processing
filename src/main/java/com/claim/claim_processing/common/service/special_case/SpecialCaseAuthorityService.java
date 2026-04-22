package com.claim.claim_processing.common.service.special_case;

import com.claim.claim_processing.common.DTO.request.special_case.SpecialCaseAuthorityRequestDto;
import com.claim.claim_processing.common.DTO.response.special_case.SpecialCaseAuthorityResponseDto;
import com.claim.claim_processing.common.DTO.update.special_case.SpecialCaseAuthorityUpdateRequestDto;

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