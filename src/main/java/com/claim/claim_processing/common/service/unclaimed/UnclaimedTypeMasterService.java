package com.claim.claim_processing.common.service.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedTypeResponseDto;

import java.util.List;

public interface UnclaimedTypeMasterService {

    UnclaimedTypeResponseDto create(UnclaimedTypeRequestDto dto);

    UnclaimedTypeResponseDto update(Long id, UnclaimedTypeRequestDto dto);

    UnclaimedTypeResponseDto getById(Long id);

    UnclaimedTypeResponseDto getByCode(String code);

    List<UnclaimedTypeResponseDto> getAll();

    List<UnclaimedTypeResponseDto> getAllActive();

    void delete(Long id);
}