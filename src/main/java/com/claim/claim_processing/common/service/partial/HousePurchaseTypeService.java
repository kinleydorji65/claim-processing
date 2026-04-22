package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.HousePurchaseTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.HousePurchaseTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.HousePurchaseTypeUpdateDto;

import java.util.List;

public interface HousePurchaseTypeService {

    HousePurchaseTypeResponseDto create(HousePurchaseTypeRequestDto requestDto);

    HousePurchaseTypeResponseDto getById(Long id);

    HousePurchaseTypeResponseDto getByCode(String code);

    List<HousePurchaseTypeResponseDto> getAll();

    List<HousePurchaseTypeResponseDto> getAllActive();

    HousePurchaseTypeResponseDto update(Long id, HousePurchaseTypeUpdateDto updateDto);

    void delete(Long id);
}