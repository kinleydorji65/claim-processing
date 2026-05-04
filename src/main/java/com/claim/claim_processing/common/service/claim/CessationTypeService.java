package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.CessationTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.update.claim.CessationTypeUpdateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.CessationTypeResponseDto;

import java.util.List;

public interface CessationTypeService {

    CessationTypeResponseDto create(CessationTypeCreateRequestDto dto);

    CessationTypeResponseDto update(Long id, CessationTypeUpdateRequestDto dto);

    CessationTypeResponseDto getById(Long id);

    List<CessationTypeResponseDto> getAll();

    List<CessationTypeResponseDto> getActive();

    List<CessationTypeResponseDto> getByClaimCircumstance(Long circumstanceId);

    void delete(Long id);
}