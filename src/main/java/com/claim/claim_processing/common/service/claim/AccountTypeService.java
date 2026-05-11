package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.update.claim.AccountTypeUpdateRequestDto;
import com.claim.claim_processing.common.DTO.request.claim.AccountTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.AccountTypeResponseDto;

import java.util.List;

public interface AccountTypeService {

    ApiResponseDTO<List<AccountTypeResponseDto>> getAllActive();

    ApiResponseDTO<AccountTypeResponseDto> getById(Long id);

    ApiResponseDTO<AccountTypeResponseDto> create(
            AccountTypeCreateRequestDto requestDto);

    ApiResponseDTO<AccountTypeResponseDto> update(
            Long id,
            AccountTypeUpdateRequestDto requestDto);

    ApiResponseDTO<String> deactivate(Long id);
}
