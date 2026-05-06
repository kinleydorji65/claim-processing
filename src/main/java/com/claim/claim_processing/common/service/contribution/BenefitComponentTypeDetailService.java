package com.claim.claim_processing.common.service.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentDetailRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentDetailResponseDto;

import java.util.List;

public interface BenefitComponentTypeDetailService {

    BenefitComponentDetailResponseDto create(BenefitComponentDetailRequestDto dto);

    BenefitComponentDetailResponseDto update(Long id, BenefitComponentDetailRequestDto dto);

    BenefitComponentDetailResponseDto getById(Long id);

    List<BenefitComponentDetailResponseDto> getAllActive();

    List<BenefitComponentDetailResponseDto> getByBenefitComponentTypeId(Long id);

    List<BenefitComponentDetailResponseDto> getByComponentId(Long id);

    void delete(Long id);
}