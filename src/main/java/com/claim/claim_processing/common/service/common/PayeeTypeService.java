package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.PayeeTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.common.PayeeTypeResponseDto;

import java.util.List;

public interface PayeeTypeService {

    ApiResponseDTO<PayeeTypeResponseDto> create(PayeeTypeRequestDto dto);

    ApiResponseDTO<PayeeTypeResponseDto> patch(Long id, PayeeTypeRequestDto dto);

    ApiResponseDTO<PayeeTypeResponseDto> getById(Long id);

    ApiResponseDTO<PayeeTypeResponseDto> getByCode(String code);

    ApiResponseDTO<List<PayeeTypeResponseDto>> getAll();

    ApiResponseDTO<List<PayeeTypeResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(Long id);
}