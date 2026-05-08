package com.claim.claim_processing.common.service.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedStatusResponseDto;

import java.util.List;

public interface UnclaimedStatusMasterService {

    UnclaimedStatusResponseDto create(UnclaimedStatusRequestDto dto);

    UnclaimedStatusResponseDto update(Long id, UnclaimedStatusRequestDto dto);

    UnclaimedStatusResponseDto getById(Long id);

    UnclaimedStatusResponseDto getByCode(String code);

    List<UnclaimedStatusResponseDto> getAll();

    List<UnclaimedStatusResponseDto> getAllActive();

    void delete(Long id);
}