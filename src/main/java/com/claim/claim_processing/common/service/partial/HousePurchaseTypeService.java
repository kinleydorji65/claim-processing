package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.HousePurchaseTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.HousePurchaseTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.HousePurchaseTypeUpdateDto;

import java.util.List;

public interface HousePurchaseTypeService {

    ApiResponseDTO<HousePurchaseTypeResponseDto> create(HousePurchaseTypeRequestDto requestDto);

    ApiResponseDTO<HousePurchaseTypeResponseDto> getById(Long id);

    ApiResponseDTO<HousePurchaseTypeResponseDto> getByCode(String code);

    ApiResponseDTO<List<HousePurchaseTypeResponseDto>> getAll();

    ApiResponseDTO<List<HousePurchaseTypeResponseDto>> getAllActive();

    ApiResponseDTO<HousePurchaseTypeResponseDto> update(Long id, HousePurchaseTypeUpdateDto updateDto);

    ApiResponseDTO<String> delete(
            Long id
    );
}