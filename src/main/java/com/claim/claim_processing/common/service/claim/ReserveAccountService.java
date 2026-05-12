package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;

import java.util.List;

public interface ReserveAccountService {

    ApiResponseDTO<ReserveAccountResponseDto> create(
            ReserveAccountRequestDto dto
    );


    ApiResponseDTO<ReserveAccountResponseDto> update(
            Long id,
            ReserveAccountRequestDto dto
    );

    ApiResponseDTO<ReserveAccountResponseDto> getById(
            Long id
    );

    ApiResponseDTO<List<ReserveAccountResponseDto>> getAll();

    ApiResponseDTO<String> delete(
            Long id
    );


    ApiResponseDTO<List<ReserveAccountResponseDto>> getByAccountTypeId(
            Long accountTypeId
    );

    ApiResponseDTO<List<ReserveAccountResponseDto>> getBySchemeTypeId(
            Long schemeTypeId
    );
}