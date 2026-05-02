package com.claim.claim_processing.common.service.common;

import com.claim.claim_processing.common.DTO.request.common.DeductionReferenceTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DeductionReferenceTypeResponseDto;

import java.util.List;

public interface DeductionReferenceTypeService {

    DeductionReferenceTypeResponseDto create(DeductionReferenceTypeRequestDto dto);

    DeductionReferenceTypeResponseDto update(Long id, DeductionReferenceTypeRequestDto dto);

    DeductionReferenceTypeResponseDto getById(Long id);

    DeductionReferenceTypeResponseDto getByCode(String code);

    List<DeductionReferenceTypeResponseDto> getAll();

    List<DeductionReferenceTypeResponseDto> getAllActive();

    void delete(Long id);
}