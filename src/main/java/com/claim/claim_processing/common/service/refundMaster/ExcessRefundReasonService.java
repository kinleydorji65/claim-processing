package com.claim.claim_processing.common.service.refundMaster;

import com.claim.claim_processing.common.DTO.request.refundMaster.ExcessRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.refundMaster.ExcessRefundReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.refundMaster.ExcessRefundReasonUpdateDto;

import java.util.List;

public interface ExcessRefundReasonService {

    ExcessRefundReasonResponseDto create(ExcessRefundReasonRequestDto requestDto);

    ExcessRefundReasonResponseDto getById(Long id);

    ExcessRefundReasonResponseDto getByCode(String code);

    List<ExcessRefundReasonResponseDto> getAll();

    List<ExcessRefundReasonResponseDto> getAllActive();

    ExcessRefundReasonResponseDto update(Long id, ExcessRefundReasonUpdateDto updateDto);

    void delete(Long id);
}