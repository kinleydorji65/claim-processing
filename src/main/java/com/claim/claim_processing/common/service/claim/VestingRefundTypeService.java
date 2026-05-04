package com.claim.claim_processing.common.service.claim;

import com.claim.claim_processing.common.DTO.request.claim.VestingRefundTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.VestingRefundTypeResponseDto;

import java.util.List;

public interface VestingRefundTypeService {

    VestingRefundTypeResponseDto create(VestingRefundTypeRequestDto requestDto);

    VestingRefundTypeResponseDto update(Long id, VestingRefundTypeRequestDto requestDto);

    VestingRefundTypeResponseDto getById(Long id);

    List<VestingRefundTypeResponseDto> getAll();

    void delete(Long id);
}