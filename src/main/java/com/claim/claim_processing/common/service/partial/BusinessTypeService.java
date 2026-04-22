package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.BusinessTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.BusinessTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.BusinessTypeUpdateDto;

import java.util.List;

public interface BusinessTypeService {

    BusinessTypeResponseDto create(BusinessTypeRequestDto requestDto);

    BusinessTypeResponseDto getById(Long id);

    BusinessTypeResponseDto getByCode(String code);

    List<BusinessTypeResponseDto> getAll();

    List<BusinessTypeResponseDto> getAllActive();

    BusinessTypeResponseDto update(Long id, BusinessTypeUpdateDto updateDto);

    void delete(Long id);
}