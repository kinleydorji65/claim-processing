package com.claim.claim_processing.common.service.refundMaster;

import com.claim.claim_processing.common.DTO.request.refundMaster.ExcessRefundReasonRequestDto;
import com.claim.claim_processing.common.DTO.response.ApiResponseDTO;
import com.claim.claim_processing.common.DTO.response.refundMaster.ExcessRefundReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.refundMaster.ExcessRefundReasonUpdateDto;

import java.util.List;

public interface ExcessRefundReasonService {

    ApiResponseDTO<ExcessRefundReasonResponseDto> create(ExcessRefundReasonRequestDto requestDto);

    ApiResponseDTO<ExcessRefundReasonResponseDto> getById(Long id);

    ApiResponseDTO<ExcessRefundReasonResponseDto> getByCode(String code);

    ApiResponseDTO<List<ExcessRefundReasonResponseDto>> getAll();

    ApiResponseDTO<List<ExcessRefundReasonResponseDto>> getAllActive();

    ApiResponseDTO<ExcessRefundReasonResponseDto> update(Long id, ExcessRefundReasonUpdateDto updateDto);

    ApiResponseDTO<String> delete(
            Long id
    );
}