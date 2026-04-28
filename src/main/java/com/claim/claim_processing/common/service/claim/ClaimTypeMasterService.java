package com.claim.claim_processing.common.service.claim;
import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeMasterResponseDto;

import java.util.List;

public interface ClaimTypeMasterService {

    ClaimTypeMasterResponseDto create(ClaimTypeMasterRequestDto requestDto);

    ClaimTypeMasterResponseDto update(Long id, ClaimTypeMasterRequestDto requestDto);

    ClaimTypeMasterResponseDto getById(Long id);

    ClaimTypeMasterResponseDto getByCode(String code);

    List<ClaimTypeMasterResponseDto> getAll();

    List<ClaimTypeMasterResponseDto> getAllActive();

    void delete(Long id);
}