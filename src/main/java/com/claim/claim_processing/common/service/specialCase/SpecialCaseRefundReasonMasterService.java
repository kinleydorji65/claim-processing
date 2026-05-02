package com.claim.claim_processing.common.service.specialCase;

import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseRefundReasonResponseDto;
import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseRefundReasonRequestDto;

import java.util.List;

public interface SpecialCaseRefundReasonMasterService {

    SpecialCaseRefundReasonResponseDto create(SpecialCaseRefundReasonRequestDto requestDto);

    SpecialCaseRefundReasonResponseDto update(Long id, SpecialCaseRefundReasonRequestDto requestDto);

    SpecialCaseRefundReasonResponseDto patch(Long id, SpecialCaseRefundReasonRequestDto requestDto);

    SpecialCaseRefundReasonResponseDto getById(Long id);

    SpecialCaseRefundReasonResponseDto getByCode(String code);

    List<SpecialCaseRefundReasonResponseDto> getAll();

    List<SpecialCaseRefundReasonResponseDto> getAllActive();

    void delete(Long id);
}