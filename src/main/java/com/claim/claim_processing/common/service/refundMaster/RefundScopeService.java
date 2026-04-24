package com.claim.claim_processing.common.service.refundMaster;

import com.claim.claim_processing.common.DTO.request.refund_master.RefundScopeRequestDto;
import com.claim.claim_processing.common.DTO.response.refundMaster.RefundScopeResponseDto;
import com.claim.claim_processing.common.DTO.update.refund_master.RefundScopeUpdateDto;

import java.util.List;

public interface RefundScopeService {

    RefundScopeResponseDto create(RefundScopeRequestDto requestDto);

    RefundScopeResponseDto getById(Long id);

    RefundScopeResponseDto getByCode(String code);

    List<RefundScopeResponseDto> getAll();

    List<RefundScopeResponseDto> getAllActive();

    RefundScopeResponseDto update(Long id, RefundScopeUpdateDto updateDto);

    void delete(Long id);
}