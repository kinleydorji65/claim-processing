package com.claim.claim_processing.common.service.partial;

import com.claim.claim_processing.common.DTO.request.partial.BusinessTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.partial.BusinessTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.BusinessTypeUpdateDto;

import java.util.List;

public interface BusinessTypeService {

    ApiResponseDTO<BusinessTypeResponseDto> create(BusinessTypeRequestDto requestDto);

    ApiResponseDTO<BusinessTypeResponseDto> getById(Long id);

    ApiResponseDTO<BusinessTypeResponseDto> getByCode(String code);

    ApiResponseDTO<List<BusinessTypeResponseDto>> getAll();

    ApiResponseDTO<List<BusinessTypeResponseDto>> getAllActive();

    ApiResponseDTO<BusinessTypeResponseDto> update(Long id, BusinessTypeUpdateDto updateDto);

    ApiResponseDTO<String> delete(
            Long id
    );
}