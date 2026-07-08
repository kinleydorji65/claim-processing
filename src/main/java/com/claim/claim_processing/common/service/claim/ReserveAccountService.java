package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;

import java.util.List;

import java.math.BigDecimal;

public interface ReserveAccountService {

    // CRUD Operations
    ApiResponseDTO<ReserveAccountResponseDto> create(ReserveAccountRequestDto dto);

    ApiResponseDTO<ReserveAccountResponseDto> update(Long id, ReserveAccountRequestDto dto);

    ApiResponseDTO<ReserveAccountResponseDto> getById(Long id);

    ApiResponseDTO<List<ReserveAccountResponseDto>> getAll();

    ApiResponseDTO<String> delete(Long id);

    // Search Operations
    ApiResponseDTO<List<ReserveAccountResponseDto>> getByNppfNumber(String nppfNumber);

    ApiResponseDTO<List<ReserveAccountResponseDto>> getByIdentityNumber(String identityNumber);

    ApiResponseDTO<List<ReserveAccountResponseDto>> getByStatus(String status);
}