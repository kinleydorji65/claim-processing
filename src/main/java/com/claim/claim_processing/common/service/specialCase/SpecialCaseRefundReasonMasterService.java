package com.claim.claim_processing.common.service.specialCase;

import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseRefundReasonResponseDto;
import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseRefundReasonRequestDto;

import java.util.List;

public interface SpecialCaseRefundReasonMasterService {

    ApiResponseDTO<SpecialCaseRefundReasonResponseDto> create(SpecialCaseRefundReasonRequestDto requestDto);

    ApiResponseDTO<SpecialCaseRefundReasonResponseDto> update(Long id, SpecialCaseRefundReasonRequestDto requestDto);

    ApiResponseDTO<SpecialCaseRefundReasonResponseDto> getById(Long id);

    ApiResponseDTO<SpecialCaseRefundReasonResponseDto> getByCode(String code);

    ApiResponseDTO<List<SpecialCaseRefundReasonResponseDto>> getAll();

    ApiResponseDTO<List<SpecialCaseRefundReasonResponseDto>> getAllActive();

    ApiResponseDTO<String> delete(
            Long id
    );
}