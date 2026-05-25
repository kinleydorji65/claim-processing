package com.claim.claim_processing.common.service.specialCase;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseAuthorityRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseAuthorityResponseDto;
import com.claim.claim_processing.common.DTO.update.specialCase.SpecialCaseAuthorityUpdateRequestDto;

import java.util.List;

public interface SpecialCaseAuthorityService {

    ApiResponseDTO<SpecialCaseAuthorityResponseDto> create(SpecialCaseAuthorityRequestDto requestDto);

    ApiResponseDTO<SpecialCaseAuthorityResponseDto> getById(Long id);

    ApiResponseDTO<SpecialCaseAuthorityResponseDto> getByCode(String code);

    ApiResponseDTO<List<SpecialCaseAuthorityResponseDto>> getAll();

    ApiResponseDTO<List<SpecialCaseAuthorityResponseDto>> getAllActive();

    ApiResponseDTO<SpecialCaseAuthorityResponseDto> update(Long id, SpecialCaseAuthorityUpdateRequestDto updateRequestDto);

    ApiResponseDTO<String> delete(
            Long id
    );
}