package com.claim.claim_processing.common.service.others;

import com.claim.claim_processing.common.DTO.request.others.NppfOfficeRequestDto;
import com.claim.claim_processing.common.DTO.response.others.NppfOfficeResponseDto;
import com.claim.claim_processing.common.DTO.update.others.NppfOfficeUpdateDto;

import java.util.List;

public interface NppfOfficeService {

    NppfOfficeResponseDto create(NppfOfficeRequestDto requestDto);

    NppfOfficeResponseDto getById(Long id);

    NppfOfficeResponseDto getByCode(Long code);

    List<NppfOfficeResponseDto> getAll();

    List<NppfOfficeResponseDto> getAllActive();

    NppfOfficeResponseDto update(Long id, NppfOfficeUpdateDto updateDto);

    void delete(Long id);
}