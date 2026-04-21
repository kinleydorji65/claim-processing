package com.claim.claim_processing.common.service.beneficiary;
import com.claim.claim_processing.common.DTO.response.beneficiary.ClaimantTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.beneficiary.ClaimantTypeUpdateRequestDto;
import com.claim.claim_processing.common.DTO.request.beneficiary.ClaimantTypeCreateRequestDto;

import java.util.List;
public interface ClaimantTypeService {

    List<ClaimantTypeResponseDto> getAllActive();

    ClaimantTypeResponseDto getById(Long id);

    ClaimantTypeResponseDto create(ClaimantTypeCreateRequestDto requestDto);

    ClaimantTypeResponseDto update(Long id, ClaimantTypeUpdateRequestDto requestDto);

    void deactivate(Long id);
}
