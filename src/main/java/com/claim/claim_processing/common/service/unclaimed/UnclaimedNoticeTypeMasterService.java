package com.claim.claim_processing.common.service.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedNoticeTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedNoticeTypeResponseDto;

import java.util.List;

public interface UnclaimedNoticeTypeMasterService {

    UnclaimedNoticeTypeResponseDto create(UnclaimedNoticeTypeRequestDto dto);

    UnclaimedNoticeTypeResponseDto update(Long id, UnclaimedNoticeTypeRequestDto dto);

    UnclaimedNoticeTypeResponseDto getById(Long id);

    UnclaimedNoticeTypeResponseDto getByCode(String code);

    List<UnclaimedNoticeTypeResponseDto> getAll();

    List<UnclaimedNoticeTypeResponseDto> getAllActive();

    void delete(Long id);
}